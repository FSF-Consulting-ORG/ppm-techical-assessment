package com.thibaultdacla.ppm_technical_assessment

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import io.branch.indexing.BranchUniversalObject
import io.branch.referral.Branch
import io.branch.referral.Branch.BranchLinkCreateListener
import io.branch.referral.BranchError
import io.branch.referral.util.LinkProperties
import org.json.JSONObject


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Branch logging for debugging
        Branch.enableTestMode()
        // Branch object initialization
        Branch.getAutoInstance(this)

        setContent {
            Scaffold(
                topBar = { TopAppBar(title = { Text("PPM Technical Assessment\n(Thibault Dacla)", color = Color.White) }, backgroundColor = Color(0xff0f9d58)) },
                content = {
                    Column(
                        Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Button(
                            onClick = {
                                createAndShare()
                            },
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color(0XFF0F9D58)),
                        ) {
                            Text("Share", color = Color.White)
                        }
                    }
                }
            )
        }
    }

    override fun onStart() {
        super.onStart()
        // Branch init
        Branch.sessionBuilder(this).withCallback(branchListener).withData(this.intent?.data).init()
        Branch.getInstance().disableTracking(true)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        this.intent = intent

        // if activity is in foreground (or in backstack but partially visible) launch the same
        // activity will skip onStart, handle this case with reInit
        if (intent != null &&
            intent.hasExtra("branch_force_new_session") &&
            intent.getBooleanExtra("branch_force_new_session", false)
        ) {
            Branch.sessionBuilder(this).withCallback(branchListener).reInit()

        }
    }

    private val branchListener = object: Branch.BranchReferralInitListener {
        override fun onInitFinished(referringParams: JSONObject?, error: BranchError?){
            if (error == null) {
                Log.i("BRANCH_SDK", referringParams.toString())

                if(referringParams?.has("deep_link_test") == true){
                    val dlt_value: String = referringParams.get("deep_link_test").toString()
                    if(dlt_value == "other"){
                        goToOtherActivity()
                    }else{
                        Log.i("T_DACLA", "deep_link_test found in the linkParameters, but not with 'other' value")
                    }
                } else {
                    Log.i("T_DACLA", "deep_link_test not found in the linkParameters")
                }

            } else {
                Log.e("BRANCH_SDK", error.message)
            }
        }
    }

    fun goToOtherActivity(){
        // Open the other activity
        val intentOther = Intent(this, OtherActivity::class.java)
        this.startActivity(intentOther)
    }

    fun createAndShare(){

        // THIS PART WAS INSPIRED BY THE TEST BED APP (with some adaptations) BUT DOESN'T SEEM TO
        // WORK WITH THE KEY PROVIDED (ONLY WORKS WITH THE TEST KEY)

        val buo = BranchUniversalObject()
            .setCanonicalIdentifier("content/12345")
            .setTitle("My Content Title")
            .setContentDescription("My Content Description")
            .setContentImageUrl("https://lorempixel.com/400/400")
            .setContentIndexingMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
            .setLocalIndexMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)

        val lp = LinkProperties()
            .addTag("Tag1")
            .setChannel("Sharing_Channel_name")
            .setFeature("my_feature_name")
            .addControlParameter("\$android_deeplink_path", "custom/path/*")
            .addControlParameter("\$ios_url", "http://example.com/ios")
            .addControlParameter("deep_link_test", "other")
            .setDuration(100)

        // Async Link creation example
        buo.generateShortUrl(this@MainActivity, lp,
            BranchLinkCreateListener { url, error ->
                if (error != null) {
                    Log.e("BRANCH_SDK", error.message)
                } else {
                    copyTextToClipboard(url.toString())
                    Log.i("T_DACLA", "URL: "+url)
                }
            })


        // THIS PART WAS COPIED FROM THE DOCUMENTATION (with some adaptations) BUT DOESN'T WORK

//        val buo = BranchUniversalObject()
//            .setCanonicalIdentifier("content/12345")
//            .setTitle("My Content Title")
//            .setContentDescription("My Content Description")
//            .setContentImageUrl("https://lorempixel.com/400/400")
//            .setContentIndexingMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
//            .setLocalIndexMode(BranchUniversalObject.CONTENT_INDEX_MODE.PUBLIC)
//
//        val lp = LinkProperties()
//            .setChannel("facebook")
//            .setFeature("sharing")
//            .setCampaign("content 123 launch")
//            .setStage("new user")
//            .addControlParameter("$android_deeplink_path", "custom/path/*")
//            .addControlParameter("deep_link_test", "other")

//        buo.generateShortUrl(this, lp) { url, error ->
//            if (error == null) {
//                Log.i("BRANCH SDK", "got my Branch link to share: $url")
//            }
//        }
//
//        val ss = ShareSheetStyle(this@MainActivity, "Check this out!", "This stuff is awesome: ")
//            .setCopyUrlStyle(getDrawable(android.R.drawable.ic_menu_send), "Copy", "Added to clipboard")
//            .setMoreOptionStyle(getDrawable(android.R.drawable.ic_menu_search), "Show more")
//            .addPreferredSharingOption(SharingHelper.SHARE_WITH.FACEBOOK)
//            .addPreferredSharingOption(SharingHelper.SHARE_WITH.EMAIL)
//            .addPreferredSharingOption(SharingHelper.SHARE_WITH.MESSAGE)
//            .addPreferredSharingOption(SharingHelper.SHARE_WITH.HANGOUT)
//            .setAsFullWidthStyle(true)
//            .setSharingTitle("Share : ")
//
//        buo.showShareSheet(this, lp, ss, object : Branch.BranchLinkShareListener {
//            override fun onShareLinkDialogLaunched() {}
//            override fun onShareLinkDialogDismissed() {}
//            override fun onLinkShareResponse(sharedLink: String?, sharedChannel: String?, error: BranchError?) {}
//            override fun onChannelSelected(channelName: String) {}
//        })

    }

    private fun copyTextToClipboard(text:String) {
        val clipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("label", text)
        clipboardManager.setPrimaryClip(clipData)
        Toast.makeText(this, "Deep Link copied to clipboard", Toast.LENGTH_LONG).show()
    }
}

// For displaying preview in
// the Android Studio IDE emulator
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MainActivity()
}
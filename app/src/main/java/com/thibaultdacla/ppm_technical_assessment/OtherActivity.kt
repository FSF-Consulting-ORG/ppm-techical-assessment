package com.thibaultdacla.ppm_technical_assessment

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.thibaultdacla.ppm_technical_assessment.ui.theme.PPMtechnicalassessmentTheme


class OtherActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Scaffold(
                topBar = { TopAppBar(title = { Text("PPM Technical Assessment\n(Thibault Dacla)", color = Color.White) }, backgroundColor = Color(0xff0f9d58)) },
                content = { DeepLinkScreen() }
            )
        }
    }
}

@Composable
fun DeepLinkScreen() {
    Text(text = "deep link screen")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview2() {
    PPMtechnicalassessmentTheme {
        DeepLinkScreen()
    }
}
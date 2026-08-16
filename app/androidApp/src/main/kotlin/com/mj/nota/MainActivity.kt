package com.mj.nota

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.mj.nota.db.DriverFactory
import com.mj.nota.util.AndroidTextToSpeechHelper

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            val context = androidx.compose.ui.platform.LocalContext.current
            val ttsHelper = remember { AndroidTextToSpeechHelper(context) }
            App(
                driverFactory = DriverFactory(applicationContext),
                ttsHelper = ttsHelper
            )
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App(DriverFactory(androidx.compose.ui.platform.LocalContext.current))
}

package com.nota

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.nota.db.DriverFactory

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            App(DriverFactory(applicationContext))
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App(DriverFactory(androidx.compose.ui.platform.LocalContext.current))
}

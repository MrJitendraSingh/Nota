package com.nota

import androidx.compose.ui.window.ComposeUIViewController
import com.nota.db.DriverFactory
import com.nota.util.createTtsHelper

fun MainViewController() = ComposeUIViewController { 
    App(
        driverFactory = DriverFactory(), 
        ttsHelper = createTtsHelper()
    ) 
}
package com.mj.nota

import androidx.compose.ui.window.ComposeUIViewController
import com.mj.nota.db.DriverFactory
import com.mj.nota.util.createTtsHelper

fun MainViewController() = ComposeUIViewController { 
    App(
        driverFactory = DriverFactory(), 
        ttsHelper = createTtsHelper()
    ) 
}
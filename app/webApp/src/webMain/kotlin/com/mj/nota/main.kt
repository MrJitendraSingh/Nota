package com.mj.nota

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.mj.nota.db.DriverFactory
import com.mj.nota.util.createTtsHelper

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport("compose-root") {
        App(
            driverFactory = DriverFactory(),
            ttsHelper = createTtsHelper()
        )
    }
}
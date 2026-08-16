package com.nota

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.nota.db.DriverFactory


@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport("compose-root") {
        App(DriverFactory())
    }
}
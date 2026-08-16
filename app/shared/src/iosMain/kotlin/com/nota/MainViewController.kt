package com.nota

import androidx.compose.ui.window.ComposeUIViewController
import com.nota.db.DriverFactory


fun MainViewController() = ComposeUIViewController { App(DriverFactory()) }
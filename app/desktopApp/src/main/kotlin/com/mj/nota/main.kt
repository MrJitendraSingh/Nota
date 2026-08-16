package com.mj.nota

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.mj.nota.db.DriverFactory
import com.mj.nota.util.createTtsHelper
import org.jetbrains.compose.resources.painterResource
import com.mj.nota.app.shared.Res
import com.mj.nota.app.shared.app_icon

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Nota",
        icon = painterResource(Res.drawable.app_icon),
    ) {
        App(
            driverFactory = DriverFactory(),
            ttsHelper = createTtsHelper()
        )
    }
}
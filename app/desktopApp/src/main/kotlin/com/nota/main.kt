package com.nota

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.nota.db.DriverFactory
import org.jetbrains.compose.resources.painterResource
import nota.app.shared.generated.resources.Res
import nota.app.shared.generated.resources.app_icon

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Nota",
        icon = painterResource(Res.drawable.app_icon),
    ) {
        App(DriverFactory())
    }
}
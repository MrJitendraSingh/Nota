import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

dependencies {
    implementation(project(":app:shared"))

    implementation(compose.desktop.currentOs)
    implementation(libs.kotlinx.coroutinesSwing)

    implementation(libs.compose.uiToolingPreview)
    implementation(libs.compose.components.resources)
}

compose.desktop {
    application {
        mainClass = "com.nota.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.nota"
            packageVersion = "1.0.0"

            linux {
                iconFile.set(project.file("../androidApp/src/main/ic_launcher-playstore.png"))
            }
            // For Windows and macOS, you usually need .ico and .icns respectively.
            // Using the PNG here as a fallback, though it might require manual conversion.
        }
    }
}
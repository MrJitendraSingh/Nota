package com.mj.nota.di

import androidx.compose.runtime.staticCompositionLocalOf

val LocalCoreModule = staticCompositionLocalOf<CoreModule> {
    error("No CoreModule provided")
}

package com.nota

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment

import com.nota.theme.NotaTheme
import com.nota.ui.home.HomeScreen
import com.nota.ui.home.viewmodel.HomeViewModel
import com.nota.ui.landing.LandingScreen
import com.nota.di.CoreModule
import com.nota.di.LocalCoreModule
import com.nota.db.DriverFactory


@Composable
fun App(driverFactory: DriverFactory) {
    val coreModule = remember { 
        println("App: Creating CoreModule...")
        CoreModule(driverFactory)
    }
    
    val homeViewModel = remember(coreModule) {
        HomeViewModel(coreModule.getNotesUseCase)
    }
    
    LaunchedEffect(coreModule) {
        coreModule.dataSyncService.syncData()
    }

    NotaTheme {
        var currentScreen by remember { mutableStateOf("landing") }

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            CompositionLocalProvider(LocalCoreModule provides coreModule) {
                when (currentScreen) {
                    "landing" -> LandingScreen(onStartClick = { currentScreen = "home" })
                    "home" -> HomeScreen(homeViewModel)
                }
            }
        }
    }
}

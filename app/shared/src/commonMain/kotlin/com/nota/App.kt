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
import com.nota.ui.common.NoteUiModel
import com.nota.ui.landing.LandingScreen
import com.nota.ui.player.PlayerScreen
import com.nota.ui.player.viewmodel.PlayerViewModel
import com.nota.ui.add.AddScreen
import com.nota.ui.add.viewmodel.AddViewModel
import com.nota.util.TextToSpeechHelper
import com.nota.di.CoreModule
import com.nota.di.LocalCoreModule
import com.nota.db.DriverFactory


@Composable
fun App(
    driverFactory: DriverFactory,
    ttsHelper: TextToSpeechHelper? = null
) {
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
        var selectedNote by remember { mutableStateOf<NoteUiModel?>(null) }

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            CompositionLocalProvider(LocalCoreModule provides coreModule) {
                when (currentScreen) {
                    "landing" -> LandingScreen(onStartClick = { currentScreen = "home" })
                    "home" -> HomeScreen(
                        viewModel = homeViewModel,
                        onNoteClick = { note ->
                            selectedNote = note
                            currentScreen = "player"
                        },
                        onAddClick = { currentScreen = "add" }
                    )
                    "player" -> {
                        val playerViewModel = remember(selectedNote) {
                            PlayerViewModel(
                                noteId = selectedNote?.id ?: "",
                                getNoteUseCase = coreModule.getNoteUseCase,
                                getNotesUseCase = coreModule.getNotesUseCase,
                                ttsHelper = ttsHelper
                            )
                        }
                        PlayerScreen(
                            viewModel = playerViewModel,
                            onBackClick = { currentScreen = "home" }
                        )
                    }
                    "add" -> {
                        val addViewModel = remember(coreModule) {
                            AddViewModel(coreModule.insertNoteUseCase)
                        }
                        AddScreen(
                            viewModel = addViewModel,
                            onBackClick = { currentScreen = "home" }
                        )
                    }
                }
            }
        }
    }
}

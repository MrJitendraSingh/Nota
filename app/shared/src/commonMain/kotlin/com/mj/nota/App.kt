package com.mj.nota

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

import com.mj.nota.theme.NotaTheme
import com.mj.nota.ui.home.HomeScreen
import com.mj.nota.ui.home.viewmodel.HomeViewModel
import com.mj.nota.ui.common.NoteUiModel
import com.mj.nota.ui.landing.LandingScreen
import com.mj.nota.ui.player.PlayerScreen
import com.mj.nota.ui.player.viewmodel.PlayerViewModel
import com.mj.nota.ui.add.AddScreen
import com.mj.nota.ui.add.viewmodel.AddViewModel
import com.mj.nota.ui.setting.SettingScreen
import com.mj.nota.util.TextToSpeechHelper
import com.mj.nota.di.CoreModule
import com.mj.nota.di.LocalCoreModule
import com.mj.nota.db.DriverFactory


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
        HomeViewModel(
            getNotesUseCase = coreModule.getNotesUseCase,
            toggleFavoriteUseCase = coreModule.toggleFavoriteUseCase,
            dataSyncService = coreModule.dataSyncService
        )
    }
    
    LaunchedEffect(coreModule) {
        coreModule.dataSyncService.syncData()
    }

    NotaTheme {
        var currentScreen by remember { mutableStateOf("home") }
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
                        onAddClick = { 
                            selectedNote = null
                            currentScreen = "add" 
                        },
                        onEditClick = { note ->
                            selectedNote = note
                            currentScreen = "add"
                        },
                        onSettingClick = {
                            currentScreen = "settings"
                        }
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
                        val addViewModel = remember(selectedNote, coreModule) {
                            AddViewModel(
                                noteId = selectedNote?.id,
                                insertNoteUseCase = coreModule.insertNoteUseCase,
                                getNoteUseCase = coreModule.getNoteUseCase,
                                deleteNoteUseCase = coreModule.deleteNoteUseCase
                            )
                        }
                        AddScreen(
                            viewModel = addViewModel,
                            onBackClick = { currentScreen = "home" }
                        )
                    }
                    "settings" -> {
                        SettingScreen(
                            onBackClick = { currentScreen = "home" }
                        )
                    }
                }
            }
        }
    }
}

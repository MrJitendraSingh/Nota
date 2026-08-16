package com.mj.nota.ui.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mj.nota.data.DataSyncService
import com.mj.nota.domain.usecase.GetNotesUseCase
import com.mj.nota.domain.usecase.ToggleFavoriteUseCase
import com.mj.nota.ui.common.NoteUiModel
import com.mj.nota.ui.common.toUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

data class HomeUiState(
    val notes: List<NoteUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val isSyncing: Boolean = false
)

class HomeViewModel(
    private val getNotesUseCase: GetNotesUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val dataSyncService: DataSyncService
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState(isLoading = true))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        println("HomeViewModel: Initializing...")
        loadNotes()
    }

    private fun loadNotes() {
        viewModelScope.launch {
            combine(
                getNotesUseCase(),
                dataSyncService.isSyncing
            ) { notes, isSyncing ->
                HomeUiState(
                    notes = notes.map { it.toUiModel() },
                    isLoading = false,
                    isSyncing = isSyncing
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun toggleFavorite(noteId: String, currentFavorite: Boolean) {
        viewModelScope.launch {
            try {
                toggleFavoriteUseCase(noteId, !currentFavorite)
            } catch (e: Throwable) {
                println("Error toggling favorite: ${e.message}")
            }
        }
    }
}

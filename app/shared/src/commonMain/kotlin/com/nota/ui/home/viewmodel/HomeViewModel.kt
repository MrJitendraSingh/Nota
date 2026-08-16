package com.nota.ui.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nota.domain.Note
import com.nota.domain.usecase.GetNotesUseCase
import com.nota.ui.common.NoteUiModel
import com.nota.ui.common.toUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class HomeUiState(
    val notes: List<NoteUiModel> = emptyList(),
    val isLoading: Boolean = false
)

class HomeViewModel(
    private val getNotesUseCase: GetNotesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState(isLoading = true))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        println("HomeViewModel: Initializing...")
        loadNotes()
    }

    private fun loadNotes() {
        viewModelScope.launch {
            try {
                getNotesUseCase().collect { notes ->
                    _uiState.value = HomeUiState(
                        notes = notes.map { it.toUiModel() },
                        isLoading = false
                    )
                }
            } catch (e: Throwable) {
                println("Error loading notes: ${e.message}")
                _uiState.value = HomeUiState(isLoading = false)
            }
        }
    }
}

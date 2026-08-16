package com.nota.ui.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nota.domain.Note
import com.nota.domain.usecase.GetNotesUseCase
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

data class NoteUiModel(
    val id: String,
    val title: String,
    val date: String,
    val isFavorite: Boolean
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

    private fun Note.toUiModel(): NoteUiModel {
        return NoteUiModel(
            id = id,
            title = title,
            date = formatTimestamp(createdAt),
            isFavorite = false // Placeholder
        )
    }

    private fun formatTimestamp(timestamp: Long): String {
        if (timestamp == 0L) return "Recently"
        try {
            val instant = Instant.fromEpochMilliseconds(timestamp)
            val dateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
            // Use 'dayOfMonth' which is fine in 0.6.1, but let's be safe and use 'dayOfMonth' from Date
            return "${dateTime.dayOfMonth} ${dateTime.month.name.lowercase().take(3)} ${dateTime.year}"
        } catch (e: Exception) {
            return "Recently"
        }
    }
}

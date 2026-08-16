package com.nota.ui.add.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nota.domain.Note
import com.nota.domain.usecase.InsertNoteUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.time.Clock

data class AddUiState(
    val title: String = "",
    val instrument: String = "",
    val scale: String = "",
    val tempo: String = "80",
    val breathTime: String = "2",
    val currentNotesInput: String = "",
    val measures: List<List<String>> = emptyList(),
    val isSaved: Boolean = false,
    val errorMessage: String? = null
)

class AddViewModel(
    private val insertNoteUseCase: InsertNoteUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddUiState())
    val uiState: StateFlow<AddUiState> = _uiState.asStateFlow()

    fun onTitleChange(value: String) { _uiState.value = _uiState.value.copy(title = value) }
    fun onInstrumentChange(value: String) { _uiState.value = _uiState.value.copy(instrument = value) }
    fun onScaleChange(value: String) { _uiState.value = _uiState.value.copy(scale = value) }
    fun onTempoChange(value: String) { _uiState.value = _uiState.value.copy(tempo = value) }
    fun onBreathTimeChange(value: String) { _uiState.value = _uiState.value.copy(breathTime = value) }
    fun onNotesInputChange(value: String) { _uiState.value = _uiState.value.copy(currentNotesInput = value) }

    fun addMeasure() {
        val input = _uiState.value.currentNotesInput
        if (input.isBlank()) return

        val newMeasure = input.split(",").map { it.trim() }.filter { it.isNotBlank() }
        if (newMeasure.isEmpty()) return

        val newMeasures = _uiState.value.measures + listOf(newMeasure)
        _uiState.value = _uiState.value.copy(
            measures = newMeasures,
            currentNotesInput = ""
        )
    }

    fun saveNote() {
        val state = _uiState.value
        if (state.title.isBlank()) {
            _uiState.value = state.copy(errorMessage = "Title is required")
            return
        }

        viewModelScope.launch {
            try {
                val now = Clock.System.now().toEpochMilliseconds()
                val note = Note(
                    id = "note_${now}",
                    version = 1,
                    title = state.title,
                    thumbnailUrl = "",
                    tempo = state.tempo.toIntOrNull() ?: 80,
                    breathTime = state.breathTime.toIntOrNull() ?: 2,
                    scale = state.scale,
                    instrument = state.instrument,
                    measures = state.measures,
                    createdAt = now
                )
                insertNoteUseCase(note)
                _uiState.value = state.copy(isSaved = true)
            } catch (e: Exception) {
                _uiState.value = state.copy(errorMessage = "Failed to save: ${e.message}")
            }
        }
    }
}

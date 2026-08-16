package com.mj.nota.ui.add.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mj.nota.domain.Note
import com.mj.nota.domain.usecase.InsertNoteUseCase
import com.mj.nota.domain.usecase.GetNoteUseCase
import com.mj.nota.domain.usecase.DeleteNoteUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlin.time.Clock

data class AddUiState(
    val id: String? = null,
    val title: String = "",
    val instrument: String = "",
    val scale: String = "",
    val tempo: String = "80",
    val breathTime: String = "2",
    val currentNotesInput: String = "",
    val measures: List<List<String>> = emptyList(),
    val isSaved: Boolean = false,
    val errorMessage: String? = null,
    val isEditMode: Boolean = false
)

class AddViewModel(
    private val noteId: String? = null,
    private val insertNoteUseCase: InsertNoteUseCase,
    private val getNoteUseCase: GetNoteUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddUiState(id = noteId, isEditMode = noteId != null))
    val uiState: StateFlow<AddUiState> = _uiState.asStateFlow()

    init {
        noteId?.let { loadNote(it) }
    }

    private fun loadNote(id: String) {
        viewModelScope.launch {
            val note = getNoteUseCase(id).firstOrNull()
            note?.let {
                _uiState.value = _uiState.value.copy(
                    title = it.title,
                    instrument = it.instrument,
                    scale = it.scale,
                    tempo = it.tempo.toString(),
                    breathTime = it.breathTime.toString(),
                    measures = it.measures
                )
            }
        }
    }

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
                    id = state.id ?: "note_${now}",
                    version = 1,
                    title = state.title,
                    thumbnailUrl = "",
                    tempo = state.tempo.toIntOrNull() ?: 80,
                    breathTime = state.breathTime.toIntOrNull() ?: 2,
                    scale = state.scale,
                    instrument = state.instrument,
                    measures = state.measures,
                    createdAt = if (state.isEditMode) 0L else now // In a real app we'd preserve createdAt
                )
                insertNoteUseCase(note)
                _uiState.value = state.copy(isSaved = true)
            } catch (e: Exception) {
                _uiState.value = state.copy(errorMessage = "Failed to save: ${e.message}")
            }
        }
    }

    fun deleteNote() {
        val id = _uiState.value.id ?: return
        viewModelScope.launch {
            try {
                deleteNoteUseCase(id)
                _uiState.value = _uiState.value.copy(isSaved = true)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(errorMessage = "Failed to delete: ${e.message}")
            }
        }
    }
}

package com.nota.ui.player.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nota.domain.Note
import com.nota.domain.usecase.GetNoteUseCase
import com.nota.ui.common.NoteUiModel
import com.nota.ui.common.toUiModel
import com.nota.util.TextToSpeechHelper
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PlayerUiState(
    val currentNote: NoteUiModel? = null,
    val isPlaying: Boolean = false,
    val isRepeating: Boolean = false,
    val progress: Float = 0f,
    val currentMeasureIndex: Int = 0,
    val currentNoteIndex: Int = 0
)

class PlayerViewModel(
    private val noteId: String,
    private val getNoteUseCase: GetNoteUseCase,
    private val ttsHelper: TextToSpeechHelper? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    private var note: Note? = null
    private var playbackJob: Job? = null

    init {
        loadNote()
    }

    private fun loadNote() {
        viewModelScope.launch {
            getNoteUseCase(noteId).collect { fetchedNote ->
                note = fetchedNote
                _uiState.value = _uiState.value.copy(
                    currentNote = fetchedNote?.toUiModel()
                )
            }
        }
    }

    fun togglePlayPause() {
        val currentState = _uiState.value
        if (currentState.isPlaying) {
            pausePlayback()
        } else {
            startPlayback()
        }
    }

    private fun startPlayback() {
        if (_uiState.value.isPlaying && playbackJob?.isActive == true) return
        
        _uiState.value = _uiState.value.copy(isPlaying = true)

        playbackJob = viewModelScope.launch {
            val measures = note?.measures ?: return@launch
            
            while (true) {
                val startM = _uiState.value.currentMeasureIndex
                val startN = _uiState.value.currentNoteIndex
                
                for (mIdx in startM until measures.size) {
                    val measure = measures[mIdx]
                    val startNoteIdx = if (mIdx == startM) startN else 0
                    
                    for (nIdx in startNoteIdx until measure.size) {
                        val noteText = measure[nIdx]
                        
                        _uiState.value = _uiState.value.copy(
                            currentMeasureIndex = mIdx,
                            currentNoteIndex = nIdx,
                            progress = calculateProgress(mIdx, nIdx, measures)
                        )

                        playNote(noteText)
                        
                        val bpm = note?.tempo?.takeIf { it > 0 } ?: 120
                        val delayTime = (60000 / bpm).toLong()
                        delay(delayTime)
                    }
                }

                if (_uiState.value.isRepeating) {
                    _uiState.value = _uiState.value.copy(
                        currentMeasureIndex = 0,
                        currentNoteIndex = 0
                    )
                    // Continue loop
                } else {
                    _uiState.value = _uiState.value.copy(
                        isPlaying = false,
                        currentMeasureIndex = 0,
                        currentNoteIndex = 0,
                        progress = 1.0f
                    )
                    break
                }
            }
        }
    }

    private fun pausePlayback() {
        playbackJob?.cancel()
        ttsHelper?.stop()
        _uiState.value = _uiState.value.copy(isPlaying = false)
    }

    private fun playNote(text: String) {
        val sanitizedText = sanitizeNoteText(text)
        println("Playing note: '$text' as '$sanitizedText'")
        ttsHelper?.speak(sanitizedText)
    }

    private fun sanitizeNoteText(text: String): String {
        // Map common notation symbols to speakable words if needed
        // For example, if 'text' is "C4", TTS might say "C four". 
        // If it's a special symbol like "-", it might be silence.
        return when (text) {
            "-" -> "" // Silence
            else -> text
        }
    }

    private fun calculateProgress(mIdx: Int, nIdx: Int, measures: List<List<String>>): Float {
        val totalNotes = measures.sumOf { it.size }
        if (totalNotes == 0) return 0f
        
        var completedNotes = 0
        for (i in 0 until mIdx) {
            completedNotes += measures[i].size
        }
        completedNotes += nIdx
        
        return completedNotes.toFloat() / totalNotes.toFloat()
    }

    fun restart() {
        pausePlayback()
        _uiState.value = _uiState.value.copy(
            currentMeasureIndex = 0,
            currentNoteIndex = 0,
            progress = 0f
        )
        startPlayback()
    }

    fun toggleRepeat() {
        _uiState.value = _uiState.value.copy(isRepeating = !_uiState.value.isRepeating)
    }

    fun playNext() {
        // Simple skip to next measure
        pausePlayback()
        val measures = note?.measures ?: return
        var nextM = _uiState.value.currentMeasureIndex + 1
        if (nextM >= measures.size) nextM = 0
        
        _uiState.value = _uiState.value.copy(
            currentMeasureIndex = nextM,
            currentNoteIndex = 0,
            progress = calculateProgress(nextM, 0, measures)
        )
        startPlayback()
    }

    fun playPrevious() {
        pausePlayback()
        val measures = note?.measures ?: return
        var prevM = _uiState.value.currentMeasureIndex - 1
        if (prevM < 0) prevM = measures.size - 1
        
        _uiState.value = _uiState.value.copy(
            currentMeasureIndex = prevM,
            currentNoteIndex = 0,
            progress = calculateProgress(prevM, 0, measures)
        )
        startPlayback()
    }

    override fun onCleared() {
        super.onCleared()
        ttsHelper?.stop()
    }
}

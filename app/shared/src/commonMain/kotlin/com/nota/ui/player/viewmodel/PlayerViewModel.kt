package com.nota.ui.player.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nota.domain.Note
import com.nota.domain.usecase.GetNoteUseCase
import com.nota.domain.usecase.GetNotesUseCase
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
    private var noteId: String,
    private val getNoteUseCase: GetNoteUseCase,
    private val getNotesUseCase: GetNotesUseCase,
    private val ttsHelper: TextToSpeechHelper? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    private var note: Note? = null
    private var playbackJob: Job? = null
    private var allNoteIds: List<String> = emptyList()
    private var noteLoadingJob: Job? = null

    init {
        loadAllNotes()
        loadNote()
    }

    private fun loadAllNotes() {
        viewModelScope.launch {
            getNotesUseCase().collect { notes ->
                allNoteIds = notes.map { it.id }
            }
        }
    }

    private fun loadNote() {
        noteLoadingJob?.cancel()
        noteLoadingJob = viewModelScope.launch {
            getNoteUseCase(noteId).collect { fetchedNote ->
                note = fetchedNote
                _uiState.value = _uiState.value.copy(
                    currentNote = fetchedNote?.toUiModel(),
                    currentMeasureIndex = 0,
                    currentNoteIndex = 0,
                    progress = 0f
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
            // Wait for note to be loaded if it's currently loading
            while (note == null && noteLoadingJob?.isActive == true) {
                delay(50)
            }
            
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
                        
                        // Dynamically adjust TTS rate based on tempo
                        // 120 BPM -> 1.0f rate, 240 BPM -> 2.0f rate, etc.
                        val ttsRate = (bpm.toFloat() / 120f).coerceIn(0.5f, 3.0f)
                        ttsHelper?.setRate(ttsRate)

                        val delayTime = (60000 / bpm).toLong()
                        delay(delayTime)
                    }

                    // Breath time pause between measures
                    val breathTime = note?.breathTime ?: 0
                    if (breathTime > 0) {
                        delay(breathTime * 1000L)
                    }
                }

                if (_uiState.value.isRepeating) {
                    _uiState.value = _uiState.value.copy(
                        currentMeasureIndex = 0,
                        currentNoteIndex = 0
                    )
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
        return when (text) {
            "-" -> "" 
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
        if (allNoteIds.isEmpty()) return
        
        pausePlayback()
        val currentIndex = allNoteIds.indexOf(noteId)
        val nextIndex = if (currentIndex < allNoteIds.size - 1) currentIndex + 1 else 0
        
        noteId = allNoteIds[nextIndex]
        note = null // Clear current note to trigger loading wait
        loadNote()
        startPlayback()
    }

    fun playPrevious() {
        if (allNoteIds.isEmpty()) return
        
        pausePlayback()
        val currentIndex = allNoteIds.indexOf(noteId)
        val prevIndex = if (currentIndex > 0) currentIndex - 1 else allNoteIds.size - 1
        
        noteId = allNoteIds[prevIndex]
        note = null // Clear current note to trigger loading wait
        loadNote()
        startPlayback()
    }

    override fun onCleared() {
        super.onCleared()
        ttsHelper?.stop()
    }
}

package com.mj.nota.data

import com.mj.nota.domain.Note
import com.mj.nota.domain.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class InMemoryNoteRepository : NoteRepository {
    private val notesFlow = MutableStateFlow<Map<String, Note>>(emptyMap())

    override fun getAllNotes(): Flow<List<Note>> {
        return notesFlow.map { it.values.toList().sortedByDescending { note -> note.createdAt } }
    }

    override fun getNoteById(id: String): Flow<Note?> {
        return notesFlow.map { it[id] }
    }

    override suspend fun insertNote(note: Note) {
        notesFlow.value = notesFlow.value + (note.id to note)
    }

    override suspend fun deleteNote(id: String) {
        notesFlow.value = notesFlow.value - id
    }

    override suspend fun updateFavorite(id: String, isFavorite: Boolean) {
        val note = notesFlow.value[id] ?: return
        notesFlow.value = notesFlow.value + (id to note.copy(isFavorite = isFavorite))
    }
}

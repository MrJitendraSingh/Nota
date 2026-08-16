package com.nota.domain

import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    fun getAllNotes(): Flow<List<Note>>
    suspend fun insertNote(note: Note)
    suspend fun deleteNote(id: String)
}

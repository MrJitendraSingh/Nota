package com.mj.nota.domain

import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    fun getAllNotes(): Flow<List<Note>>
    fun getNoteById(id: String): Flow<Note?>
    suspend fun insertNote(note: Note)
    suspend fun deleteNote(id: String)
    suspend fun updateFavorite(id: String, isFavorite: Boolean)
}

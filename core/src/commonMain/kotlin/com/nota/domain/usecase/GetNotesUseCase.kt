package com.nota.domain.usecase

import com.nota.domain.Note
import com.nota.domain.NoteRepository
import kotlinx.coroutines.flow.Flow

class GetNotesUseCase(private val repository: NoteRepository) {
    operator fun invoke(): Flow<List<Note>> {
        return repository.getAllNotes()
    }
}

package com.nota.domain.usecase

import com.nota.domain.Note
import com.nota.domain.NoteRepository
import kotlinx.coroutines.flow.Flow

class GetNoteUseCase(private val repository: NoteRepository) {
    operator fun invoke(id: String): Flow<Note?> {
        return repository.getNoteById(id)
    }
}

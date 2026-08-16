package com.mj.nota.domain.usecase

import com.mj.nota.domain.Note
import com.mj.nota.domain.NoteRepository
import kotlinx.coroutines.flow.Flow

class GetNoteUseCase(private val repository: NoteRepository) {
    operator fun invoke(id: String): Flow<Note?> {
        return repository.getNoteById(id)
    }
}

package com.mj.nota.domain.usecase

import com.mj.nota.domain.Note
import com.mj.nota.domain.NoteRepository
import kotlinx.coroutines.flow.Flow

class GetNotesUseCase(private val repository: NoteRepository) {
    operator fun invoke(): Flow<List<Note>> {
        return repository.getAllNotes()
    }
}

package com.nota.domain.usecase

import com.nota.domain.NoteRepository

class DeleteNoteUseCase(private val repository: NoteRepository) {
    suspend operator fun invoke(id: String) {
        repository.deleteNote(id)
    }
}

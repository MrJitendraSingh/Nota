package com.mj.nota.domain.usecase

import com.mj.nota.domain.NoteRepository

class DeleteNoteUseCase(private val repository: NoteRepository) {
    suspend operator fun invoke(id: String) {
        repository.deleteNote(id)
    }
}

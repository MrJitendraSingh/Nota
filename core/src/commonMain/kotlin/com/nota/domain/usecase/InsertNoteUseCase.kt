package com.nota.domain.usecase

import com.nota.domain.Note
import com.nota.domain.NoteRepository

class InsertNoteUseCase(private val repository: NoteRepository) {
    suspend operator fun invoke(note: Note) {
        repository.insertNote(note)
    }
}

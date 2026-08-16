package com.mj.nota.domain.usecase

import com.mj.nota.domain.Note
import com.mj.nota.domain.NoteRepository

class InsertNoteUseCase(private val repository: NoteRepository) {
    suspend operator fun invoke(note: Note) {
        repository.insertNote(note)
    }
}

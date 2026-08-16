package com.nota.domain.usecase

import com.nota.domain.NoteRepository

class ToggleFavoriteUseCase(private val repository: NoteRepository) {
    suspend operator fun invoke(id: String, isFavorite: Boolean) {
        repository.updateFavorite(id, isFavorite)
    }
}

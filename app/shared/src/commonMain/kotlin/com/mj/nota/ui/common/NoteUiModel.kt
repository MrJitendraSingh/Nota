package com.mj.nota.ui.common

data class NoteUiModel(
    val id: String,
    val title: String,
    val date: String,
    val isFavorite: Boolean,
    val tempo: Int = 80,
    val breathTime: Int = 2,
    val measures: List<List<String>> = emptyList()
)

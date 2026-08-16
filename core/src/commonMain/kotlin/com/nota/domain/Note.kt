package com.nota.domain

import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
data class Note(
    val id: String,
    val version: Int,
    val title: String,
    val thumbnailUrl: String,
    val tempo: Int,
    val breathTime: Int,
    val scale: String,
    val instrument: String,
    val measures: List<List<String>>,
    val createdAt: Long = 0L, // Keep for internal tracking, though not in the user's JSON example
    val isFavorite: Boolean = false
)

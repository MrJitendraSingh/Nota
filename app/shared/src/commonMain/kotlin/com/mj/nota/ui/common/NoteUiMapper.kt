package com.mj.nota.ui.common

import com.mj.nota.domain.Note
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun Note.toUiModel(): NoteUiModel {
    return NoteUiModel(
        id = id,
        title = title,
        date = formatTimestamp(createdAt),
        isFavorite = isFavorite,
        tempo = tempo,
        breathTime = breathTime,
        measures = measures
    )
}

private fun formatTimestamp(timestamp: Long): String {
    if (timestamp == 0L) return "Recently"
    try {
        val instant = Instant.fromEpochMilliseconds(timestamp)
        val dateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        return "${dateTime.dayOfMonth} ${dateTime.month.name.lowercase().take(3)} ${dateTime.year}"
    } catch (e: Exception) {
        return "Recently"
    }
}

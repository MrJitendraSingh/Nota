package com.mj.nota.data

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.mj.nota.db.NotaDatabase
import com.mj.nota.domain.Note
import com.mj.nota.domain.NoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SqlDelightNoteRepository(database: NotaDatabase) : NoteRepository {
    private val queries = database.notaDatabaseQueries

    override fun getAllNotes(): Flow<List<Note>> {
        return queries.selectAllNotes()
            .asFlow()
            .mapToList(Dispatchers.Main) // Use Main for now to avoid iOS threading issues
            .map { entities ->
                entities.map { entity ->
                    Note(
                        id = entity.id,
                        version = entity.version,
                        title = entity.title,
                        thumbnailUrl = entity.thumbnailUrl,
                        tempo = entity.tempo,
                        breathTime = entity.breathTime,
                        scale = entity.scale,
                        instrument = entity.instrument,
                        measures = entity.measures,
                        createdAt = entity.createdAt,
                        isFavorite = entity.isFavorite
                    )
                }
            }
    }

    override fun getNoteById(id: String): Flow<Note?> {
        return queries.selectNoteById(id)
            .asFlow()
            .mapToOneOrNull(Dispatchers.Main)
            .map { entity ->
                entity?.let {
                    Note(
                        id = it.id,
                        version = it.version,
                        title = it.title,
                        thumbnailUrl = it.thumbnailUrl,
                        tempo = it.tempo,
                        breathTime = it.breathTime,
                        scale = it.scale,
                        instrument = it.instrument,
                        measures = it.measures,
                        createdAt = it.createdAt,
                        isFavorite = it.isFavorite
                    )
                }
            }
    }

    override suspend fun insertNote(note: Note) {
        queries.insertNote(
            id = note.id,
            version = note.version,
            title = note.title,
            thumbnailUrl = note.thumbnailUrl,
            tempo = note.tempo,
            breathTime = note.breathTime,
            scale = note.scale,
            instrument = note.instrument,
            measures = note.measures,
            createdAt = note.createdAt,
            isFavorite = note.isFavorite
        )
    }

    override suspend fun deleteNote(id: String) {
        queries.deleteNote(id)
    }

    override suspend fun updateFavorite(id: String, isFavorite: Boolean) {
        queries.updateFavorite(isFavorite, id)
    }
}

package com.mj.nota.di

import com.mj.nota.data.DataSyncService
import com.mj.nota.data.InMemoryNoteRepository
import com.mj.nota.data.SqlDelightNoteRepository
import com.mj.nota.db.DriverFactory
import com.mj.nota.db.createDatabase
import com.mj.nota.domain.NoteRepository
import com.mj.nota.domain.usecase.GetNoteUseCase
import com.mj.nota.domain.usecase.GetNotesUseCase
import com.mj.nota.domain.usecase.InsertNoteUseCase
import com.mj.nota.domain.usecase.ToggleFavoriteUseCase
import com.mj.nota.domain.usecase.DeleteNoteUseCase

class CoreModule(driverFactory: DriverFactory) {
    val noteRepository: NoteRepository by lazy {
        try {
            println("CoreModule: Initializing noteRepository (SqlDelight)")
            val database = createDatabase(driverFactory)
            SqlDelightNoteRepository(database)
        } catch (e: Throwable) {
            println("CoreModule: SqlDelight not supported, falling back to InMemory: ${e.message}")
            InMemoryNoteRepository()
        }
    }

    val getNotesUseCase: GetNotesUseCase by lazy {
        println("CoreModule: Initializing getNotesUseCase")
        GetNotesUseCase(noteRepository)
    }

    val getNoteUseCase: GetNoteUseCase by lazy {
        println("CoreModule: Initializing getNoteUseCase")
        GetNoteUseCase(noteRepository)
    }

    val insertNoteUseCase: InsertNoteUseCase by lazy {
        println("CoreModule: Initializing insertNoteUseCase")
        InsertNoteUseCase(noteRepository)
    }

    val toggleFavoriteUseCase: ToggleFavoriteUseCase by lazy {
        println("CoreModule: Initializing toggleFavoriteUseCase")
        ToggleFavoriteUseCase(noteRepository)
    }

    val deleteNoteUseCase: DeleteNoteUseCase by lazy {
        println("CoreModule: Initializing deleteNoteUseCase")
        DeleteNoteUseCase(noteRepository)
    }

    val dataSyncService: DataSyncService by lazy {
        println("CoreModule: Initializing dataSyncService")
        DataSyncService(noteRepository)
    }

    init {
        // Eagerly initialize to trigger fallback if necessary
        noteRepository
    }
}

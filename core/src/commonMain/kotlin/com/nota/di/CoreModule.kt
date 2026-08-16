package com.nota.di

import com.nota.data.DataSyncService
import com.nota.data.InMemoryNoteRepository
import com.nota.data.SqlDelightNoteRepository
import com.nota.db.DriverFactory
import com.nota.db.createDatabase
import com.nota.domain.NoteRepository
import com.nota.domain.usecase.GetNotesUseCase

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

    val dataSyncService: DataSyncService by lazy {
        println("CoreModule: Initializing dataSyncService")
        DataSyncService(noteRepository)
    }

    init {
        // Eagerly initialize to trigger fallback if necessary
        noteRepository
    }
}

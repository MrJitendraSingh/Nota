package com.mj.nota.db

import app.cash.sqldelight.ColumnAdapter
import app.cash.sqldelight.db.SqlDriver
import kotlinx.serialization.json.Json

expect class DriverFactory {
    fun createDriver(): SqlDriver
}

val measuresAdapter = object : ColumnAdapter<List<List<String>>, String> {
    override fun decode(databaseValue: String): List<List<String>> =
        if (databaseValue.isEmpty()) emptyList() else Json.decodeFromString(databaseValue)

    override fun encode(value: List<List<String>>): String =
        Json.encodeToString(value)
}

val intAdapter = object : ColumnAdapter<Int, Long> {
    override fun decode(databaseValue: Long): Int = databaseValue.toInt()
    override fun encode(value: Int): Long = value.toLong()
}

fun createDatabase(driverFactory: DriverFactory): NotaDatabase {
    println("DriverFactory: Creating database...")
    val driver = driverFactory.createDriver()
    println("DriverFactory: Driver created successfully")
    return NotaDatabase(
        driver = driver,
        NoteEntityAdapter = NoteEntity.Adapter(
            versionAdapter = intAdapter,
            tempoAdapter = intAdapter,
            breathTimeAdapter = intAdapter,
            measuresAdapter = measuresAdapter
        )
    )
}

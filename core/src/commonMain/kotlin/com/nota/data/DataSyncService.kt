package com.nota.data

import com.nota.domain.Note
import com.nota.domain.NoteRepository
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.datetime.Clock
import kotlinx.serialization.json.*

class DataSyncService(
    private val repository: NoteRepository,
    private val httpClient: HttpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }
) {
    // CSV export URL for the Google Sheet
    private val sheetUrl = "https://docs.google.com/spreadsheets/d/1fXvvQT9iKnX-tc5SNH2YkbPjkGssydYRRJeB0IoLOxo/export?format=csv"

    suspend fun syncData() {
        println("Starting sync from Google Sheet...")
        try {
            val response: String = httpClient.get(sheetUrl).body()
            val notes = parseCsv(response)
            println("Parsed ${notes.size} notes from CSV. Updating database...")
            notes.forEach { note ->
                repository.insertNote(note)
            }
            println("Sync completed successfully.")
        } catch (e: Throwable) {
            println("Sync failed: ${e.message}")
            e.printStackTrace()
        }
    }

    private fun parseCsv(csv: String): List<Note> {
        val lines = csv.lines()
        if (lines.size <= 1) return emptyList()

        // Assuming columns: id, version, title, thumbnailUrl, tempo, breathTime, scale, instrument, measures
        // This is a simplified parser. For production, a robust CSV library or JSON export would be better.
        
        return lines.drop(1).filter { it.isNotBlank() }.map { line ->
            val values = line.split(",")
            val now = try { Clock.System.now().toEpochMilliseconds() } catch(e: Throwable) { 0L }
            Note(
                id = values.getOrNull(0) ?: "sync_${now}",
                version = values.getOrNull(1)?.toIntOrNull() ?: 1,
                title = values.getOrNull(2) ?: "Untitled",
                thumbnailUrl = values.getOrNull(3) ?: "",
                tempo = values.getOrNull(4)?.toIntOrNull() ?: 80,
                breathTime = values.getOrNull(5)?.toIntOrNull() ?: 2,
                scale = values.getOrNull(6) ?: "C",
                instrument = values.getOrNull(7) ?: "Piano",
                measures = listOf(listOf("Sa", "Re", "Ga", "Ma")),
                createdAt = now
            )
        }
    }
}

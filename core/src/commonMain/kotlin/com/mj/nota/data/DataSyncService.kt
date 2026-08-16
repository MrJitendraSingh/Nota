package com.mj.nota.data

import com.mj.nota.domain.Note
import com.mj.nota.domain.NoteRepository
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlin.time.Clock
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    private val _isSyncing = MutableStateFlow(true)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    // CSV export URL for the Google Sheet
    private val sheetUrl = "https://docs.google.com/spreadsheets/d/1fXvvQT9iKnX-tc5SNH2YkbPjkGssydYRRJeB0IoLOxo/export?format=csv"

    suspend fun syncData() {
        println("Starting sync from Google Sheet...")
        _isSyncing.value = true
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
        } finally {
            _isSyncing.value = false
        }
    }

    private fun parseCsv(csv: String): List<Note> {
        val lines = csv.lines()
        if (lines.size <= 1) return emptyList()

        // Correct Mapping based on Sheet:
        // 0: ID
        // 1: Version
        // 2: Title
        // 3: Thumbnail URL
        // 4: Instrument
        // 5: Tempo
        // 6: Breath Time
        // 7: Scale
        // 8: Measures
        
        return lines.drop(1).filter { it.isNotBlank() }.mapNotNull { line ->
            try {
                val values = csvSplit(line)
                val now = Clock.System.now().toEpochMilliseconds()
                
                val measuresString = values.getOrNull(8) ?: ""
                val parsedMeasures = parseMeasures(measuresString)

                Note(
                    id = values.getOrNull(0) ?: "sync_${now}",
                    version = values.getOrNull(1)?.toIntOrNull() ?: 1,
                    title = values.getOrNull(2) ?: "Untitled",
                    thumbnailUrl = values.getOrNull(3) ?: "",
                    instrument = values.getOrNull(4) ?: "Piano",
                    tempo = values.getOrNull(5)?.toIntOrNull() ?: 80,
                    breathTime = values.getOrNull(6)?.toIntOrNull() ?: 2,
                    scale = values.getOrNull(7) ?: "C",
                    measures = parsedMeasures,
                    createdAt = now
                )
            } catch (e: Exception) {
                println("Error parsing line: $line - ${e.message}")
                null
            }
        }
    }

    /**
     * Splits a CSV line correctly, handling quoted values.
     */
    private fun csvSplit(line: String): List<String> {
        val result = mutableListOf<String>()
        var current = StringBuilder()
        var inQuotes = false
        
        for (char in line) {
            when {
                char == '\"' -> inQuotes = !inQuotes
                char == ',' && !inQuotes -> {
                    result.add(current.toString().trim())
                    current = StringBuilder()
                }
                else -> current.append(char)
            }
        }
        result.add(current.toString().trim())
        return result
    }

    /**
     * Parses the measures string into a List<List<String>>.
     * Example input: "Ni Re Ga Ma' Pa | Dha Ni Sa' | Sa' Ni Dha Pa Ma' Ga | Re Sa"
     */
    private fun parseMeasures(measuresStr: String): List<List<String>> {
        if (measuresStr.isBlank()) return emptyList()
        
        // Remove surrounding quotes if they exist (sometimes added by CSV export)
        val cleanStr = if (measuresStr.startsWith("\"") && measuresStr.endsWith("\"")) {
            measuresStr.substring(1, measuresStr.length - 1)
        } else {
            measuresStr
        }

        return cleanStr.split("|")
            .map { measure ->
                measure.trim().split(Regex("\\s+"))
                    .filter { it.isNotBlank() }
            }
            .filter { it.isNotEmpty() }
    }
}

package com.nota.util

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class JvmTextToSpeechHelper : TextToSpeechHelper {
    private val executor: ExecutorService = Executors.newSingleThreadExecutor()
    private var currentProcess: Process? = null

    override fun speak(text: String, onComplete: () -> Unit) {
        if (text.isBlank()) {
            onComplete()
            return
        }

        executor.execute {
            try {
                stop()
                val os = System.getProperty("os.name").lowercase()
                val process = when {
                    os.contains("mac") -> {
                        Runtime.getRuntime().exec(arrayOf("say", text))
                    }
                    os.contains("win") -> {
                        val script = "Add-Type -AssemblyName System.speech; \$speak = New-Object System.Speech.Synthesis.SpeechSynthesizer; \$speak.Speak(\"$text\")"
                        Runtime.getRuntime().exec(arrayOf("powershell", "-Command", script))
                    }
                    else -> {
                        // Linux fallback (requires espeak or festival)
                        Runtime.getRuntime().exec(arrayOf("espeak", text))
                    }
                }
                currentProcess = process
                process.waitFor()
                onComplete()
            } catch (e: Exception) {
                e.printStackTrace()
                onComplete()
            }
        }
    }

    override fun stop() {
        try {
            currentProcess?.destroy()
            currentProcess = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

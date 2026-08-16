package com.nota.util

interface TextToSpeechHelper {
    fun speak(text: String, onComplete: () -> Unit = {})
    fun stop()
    fun setRate(rate: Float) {}
}

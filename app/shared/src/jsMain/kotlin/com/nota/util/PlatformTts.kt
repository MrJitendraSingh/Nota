package com.nota.util

actual fun createTtsHelper(): TextToSpeechHelper? = WebTextToSpeechHelper()

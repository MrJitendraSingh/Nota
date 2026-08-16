package com.mj.nota.util

actual fun createTtsHelper(): TextToSpeechHelper? = WebTextToSpeechHelper()

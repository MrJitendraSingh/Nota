package com.mj.nota.util

import kotlinx.browser.window

class JsTextToSpeechHelper : TextToSpeechHelper {
    private val synth = window.asDynamic().speechSynthesis

    override fun speak(text: String, onComplete: () -> Unit) {
        if (text.isBlank()) {
            onComplete()
            return
        }

        stop()
        val utterance = js("new SpeechSynthesisUtterance()")
        utterance.text = text
        utterance.lang = "en-US"
        utterance.onend = {
            onComplete()
        }
        
        synth.speak(utterance)
    }

    override fun stop() {
        synth.cancel()
    }
}

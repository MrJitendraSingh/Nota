package com.nota.util

import kotlin.js.ExperimentalWasmJsInterop

class WebTextToSpeechHelper : TextToSpeechHelper {
    
    override fun speak(text: String, onComplete: () -> Unit) {
        if (text.isBlank()) {
            onComplete()
            return
        }
        
        speakInternal(text)
    }

    override fun stop() {
        cancelInternal()
    }
}

@OptIn(ExperimentalWasmJsInterop::class)
@JsFun("(text) => { const u = new SpeechSynthesisUtterance(text); u.lang = 'en-US'; window.speechSynthesis.speak(u); }")
private external fun speakInternal(text: String)

@OptIn(ExperimentalWasmJsInterop::class)
@JsFun("() => { window.speechSynthesis.cancel(); }")
private external fun cancelInternal()

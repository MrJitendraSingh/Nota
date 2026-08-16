package com.nota.util

import kotlinx.browser.window

class WasmTextToSpeechHelper : TextToSpeechHelper {
    
    override fun speak(text: String, onComplete: () -> Unit) {
        if (text.isBlank()) {
            onComplete()
            return
        }
        
        speakInternal(text)
        // Note: simplified as onComplete is harder to bridge in Wasm without more boilerplate
    }

    override fun stop() {
        cancelInternal()
    }
}

@JsFun("(text) => { const u = new SpeechSynthesisUtterance(text); u.lang = 'en-US'; window.speechSynthesis.speak(u); }")
private external fun speakInternal(text: String)

@JsFun("() => { window.speechSynthesis.cancel(); }")
private external fun cancelInternal()

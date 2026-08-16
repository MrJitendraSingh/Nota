package com.nota.util

import platform.AVFAudio.AVSpeechSynthesizer
import platform.AVFAudio.AVSpeechUtterance
import platform.AVFAudio.AVSpeechSynthesisVoice
import platform.AVFAudio.AVSpeechSynthesizerDelegateProtocol
import platform.AVFAudio.AVSpeechBoundary
import platform.darwin.NSObject

class IosTextToSpeechHelper : TextToSpeechHelper {
    private val synthesizer = AVSpeechSynthesizer()
    private var onCompleteCallback: (() -> Unit)? = null
    private var rate: Float = 0.5f

    private val delegate = object : NSObject(), AVSpeechSynthesizerDelegateProtocol {
        override fun speechSynthesizer(synthesizer: AVSpeechSynthesizer, didFinishSpeechUtterance: AVSpeechUtterance) {
            onCompleteCallback?.invoke()
        }
    }

    init {
        synthesizer.delegate = delegate
    }

    override fun speak(text: String, onComplete: () -> Unit) {
        if (text.isBlank()) {
            onComplete()
            return
        }
        
        onCompleteCallback = onComplete
        val utterance = AVSpeechUtterance.speechUtteranceWithString(text)
        utterance.voice = AVSpeechSynthesisVoice.voiceWithLanguage("en-US")
        utterance.rate = rate 
        
        synthesizer.speakUtterance(utterance)
    }

    override fun stop() {
        synthesizer.stopSpeakingAtBoundary(AVSpeechBoundary.AVSpeechBoundaryImmediate)
    }

    override fun setRate(rate: Float) {
        this.rate = rate
    }
}

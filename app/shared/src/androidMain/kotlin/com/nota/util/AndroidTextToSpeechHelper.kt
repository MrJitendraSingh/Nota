package com.nota.util

import android.content.Context
import android.media.AudioAttributes
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import java.util.Locale

class AndroidTextToSpeechHelper(context: Context) : TextToSpeechHelper {
    private var tts: TextToSpeech? = null
    private var isInitialized = false
    private var pendingText: String? = null
    private var onCompleteCallback: (() -> Unit)? = null

    init {
        Log.d("TTS", "Initializing TTS Engine...")
        // Use applicationContext to avoid memory leaks and potential initialization issues
        tts = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                // Try setting language to US
                val result = tts?.setLanguage(Locale.US)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.w("TTS", "Language US not supported, using default locale")
                    tts?.language = Locale.getDefault()
                }
                
                // Explicitly set audio attributes to ensure it plays on the Media stream
                val audioAttributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build()
                tts?.setAudioAttributes(audioAttributes)
                
                // Fine-tune for musical syllables
                tts?.setPitch(1.0f)
                tts?.setSpeechRate(1.1f) 

                Log.d("TTS", "TTS Initialized successfully. Language: ${tts?.language}")
                isInitialized = true
                
                pendingText?.let {
                    speak(it, onCompleteCallback ?: {})
                    pendingText = null
                }
            } else {
                Log.e("TTS", "Initialization failed with status: $status")
            }
        }

        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                Log.d("TTS", "Playback started: $utteranceId")
            }
            override fun onDone(utteranceId: String?) {
                Log.d("TTS", "Playback finished: $utteranceId")
                onCompleteCallback?.invoke()
            }
            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) {
                Log.e("TTS", "Playback error: $utteranceId")
                onCompleteCallback?.invoke()
            }
        })
    }

    override fun speak(text: String, onComplete: () -> Unit) {
        if (text.isBlank()) {
            onComplete()
            return
        }
        
        onCompleteCallback = onComplete
        if (isInitialized) {
            Log.d("TTS", "Requesting to speak: '$text'")
            
            val params = Bundle()
            params.putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, 1.0f)
            
            // Try QUEUE_ADD to see if it works better on this device
            val result = tts?.speak(text, TextToSpeech.QUEUE_ADD, params, "note_${text.hashCode()}")
            if (result == TextToSpeech.ERROR) {
                Log.e("TTS", "Speak call failed for: '$text'")
            }
        } else {
            Log.d("TTS", "TTS not ready, queuing: '$text'")
            pendingText = text
        }
    }

    override fun stop() {
        Log.d("TTS", "Stopping TTS")
        tts?.stop()
    }
}

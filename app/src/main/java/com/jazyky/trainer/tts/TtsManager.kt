package com.jazyky.trainer.tts

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale

/**
 * Obal nad Android TextToSpeech enginem.
 * Zdarma, funguje offline (pokud má uživatel stažený hlasový balíček daného jazyka v systému).
 */
class TtsManager(context: Context) {

    private var isReady = false
    private var pendingText: Pair<String, Locale>? = null

    private val engine: TextToSpeech = TextToSpeech(context.applicationContext) { status ->
        isReady = status == TextToSpeech.SUCCESS
        pendingText?.let { (text, locale) ->
            speakInternal(text, locale)
            pendingText = null
        }
    }

    fun speak(text: String, languageCode: String) {
        val locale = localeFor(languageCode)
        if (isReady) {
            speakInternal(text, locale)
        } else {
            // TTS engine se ještě inicializuje - zapamatuj si text a přehraj, až bude připravený.
            pendingText = text to locale
        }
    }

    private fun speakInternal(text: String, locale: Locale) {
        engine.language = locale
        engine.speak(text, TextToSpeech.QUEUE_FLUSH, null, "utteranceId")
    }

    private fun localeFor(languageCode: String): Locale = when (languageCode) {
        "en" -> Locale.US
        "de" -> Locale.GERMANY
        else -> Locale.getDefault()
    }

    fun shutdown() {
        engine.stop()
        engine.shutdown()
    }
}

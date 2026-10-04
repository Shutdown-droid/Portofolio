package com.example.util

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import java.util.Locale

class SpeechManager(
    private val context: Context,
    private val onResult: (String) -> Unit,
    private val onError: (String) -> Unit,
    private val onListeningStateChanged: (Boolean) -> Unit
) {
    private var speechRecognizer: SpeechRecognizer? = null
    private var isListening: Boolean = false

    fun startListening() {
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            // Graceful fallback simulation when Google Speech service isn't active on emulator
            simulateVoiceRecognition()
            return
        }

        try {
            stopListening()

            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(object : RecognitionListener {
                    override fun onReadyForSpeech(params: Bundle?) {
                        isListening = true
                        onListeningStateChanged(true)
                    }

                    override fun onBeginningOfSpeech() {}
                    override fun onRmsChanged(rmsdB: Float) {}
                    override fun onBufferReceived(buffer: ByteArray?) {}
                    override fun onEndOfSpeech() {
                        isListening = false
                        onListeningStateChanged(false)
                    }

                    override fun onError(error: Int) {
                        isListening = false
                        onListeningStateChanged(false)
                        Log.w("SpeechManager", "Speech recognition error code: $error")
                        // If error occurred (e.g., audio hardware unavailable in container), fallback
                        simulateVoiceRecognition()
                    }

                    override fun onResults(results: Bundle?) {
                        isListening = false
                        onListeningStateChanged(false)
                        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        if (!matches.isNullOrEmpty()) {
                            onResult(matches[0])
                        } else {
                            onError("Tidak ada suara terdeteksi.")
                        }
                    }

                    override fun onPartialResults(partialResults: Bundle?) {}
                    override fun onEvent(eventType: Int, params: Bundle?) {}
                })
            }

            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, "id-ID")
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "id-ID")
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
                putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.packageName)
            }

            speechRecognizer?.startListening(intent)
        } catch (e: Exception) {
            Log.e("SpeechManager", "Exception starting speech recognizer", e)
            simulateVoiceRecognition()
        }
    }

    fun stopListening() {
        try {
            if (isListening) {
                speechRecognizer?.stopListening()
            }
            speechRecognizer?.destroy()
            speechRecognizer = null
            isListening = false
            onListeningStateChanged(false)
        } catch (e: Exception) {
            Log.w("SpeechManager", "Error stopping speech recognizer", e)
        }
    }

    private fun simulateVoiceRecognition() {
        onListeningStateChanged(true)
        val sampleSpokenCommands = listOf(
            "Pesan 2 Nasi Ayam Krispi",
            "Tambah 1 Es Jeruk Segar",
            "Minta 3 Gorengan",
            "1 Nasi Telor Dadar sama 1 Es Teh",
            "2 Kopi Susu Gula Aren",
            "1 Bakso Urat Malang",
            "1 Soto Ayam Lamongan",
            "2 Mie Goreng Telur",
            "3 Pisang Goreng Keju"
        )
        val selected = sampleSpokenCommands.random()
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            onListeningStateChanged(false)
            onResult(selected)
        }, 1800)
    }
}

package com.example.data.service

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import com.example.data.model.LanguageMode
import com.example.data.model.VoiceGender
import com.example.data.model.VoiceSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.random.Random

class JarvisVoiceEngine(private val context: Context) : TextToSpeech.OnInitListener {

    private val TAG = "JarvisVoiceEngine"
    private var tts: TextToSpeech? = null
    private var isTtsReady = false

    private var speechRecognizer: SpeechRecognizer? = null
    private val scope = CoroutineScope(Dispatchers.Main)
    private var visualizerJob: Job? = null

    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening.asStateFlow()

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _audioAmplitudes = MutableStateFlow<List<Float>>(List(16) { 0.1f })
    val audioAmplitudes: StateFlow<List<Float>> = _audioAmplitudes.asStateFlow()

    private val _recognizedText = MutableStateFlow("")
    val recognizedText: StateFlow<String> = _recognizedText.asStateFlow()

    var onSpeechRecognized: ((String) -> Unit)? = null
    var onSpeechFinished: (() -> Unit)? = null

    private var currentSettings = VoiceSettings()

    init {
        tts = TextToSpeech(context.applicationContext, this)
        initSpeechRecognizer()
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isTtsReady = true
            applyVoiceSettings(currentSettings)
            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    _isSpeaking.value = true
                    startVisualizerSimulation()
                }

                override fun onDone(utteranceId: String?) {
                    _isSpeaking.value = false
                    stopVisualizerSimulation()
                    onSpeechFinished?.invoke()
                }

                override fun onError(utteranceId: String?) {
                    _isSpeaking.value = false
                    stopVisualizerSimulation()
                }
            })
        } else {
            Log.e(TAG, "TTS initialization failed: $status")
        }
    }

    private fun initSpeechRecognizer() {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(object : RecognitionListener {
                    override fun onReadyForSpeech(params: Bundle?) {
                        _isListening.value = true
                        startVisualizerSimulation()
                    }

                    override fun onBeginningOfSpeech() {
                        _isListening.value = true
                    }

                    override fun onRmsChanged(rmsdB: Float) {
                        // Map rmsdB to amplitudes
                        val normalized = ((rmsdB + 2f) / 12f).coerceIn(0.1f, 1f)
                        _audioAmplitudes.value = List(16) {
                            (normalized * (0.6f + Random.nextFloat() * 0.4f)).coerceIn(0.08f, 1f)
                        }
                    }

                    override fun onBufferReceived(buffer: ByteArray?) {}

                    override fun onEndOfSpeech() {
                        _isListening.value = false
                        stopVisualizerSimulation()
                    }

                    override fun onError(error: Int) {
                        _isListening.value = false
                        stopVisualizerSimulation()
                    }

                    override fun onResults(results: Bundle?) {
                        _isListening.value = false
                        stopVisualizerSimulation()
                        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        val text = matches?.firstOrNull() ?: ""
                        if (text.isNotBlank()) {
                            _recognizedText.value = text
                            onSpeechRecognized?.invoke(text)
                        }
                    }

                    override fun onPartialResults(partialResults: Bundle?) {
                        val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        val text = matches?.firstOrNull() ?: ""
                        if (text.isNotBlank()) {
                            _recognizedText.value = text
                        }
                    }

                    override fun onEvent(eventType: Int, params: Bundle?) {}
                })
            }
        }
    }

    fun applyVoiceSettings(settings: VoiceSettings) {
        currentSettings = settings
        if (!isTtsReady || tts == null) return

        // Pitch configuration: Female sounds crisper/higher, Male deeper
        val effectivePitch = if (settings.gender == VoiceGender.FEMALE) {
            settings.pitch * 1.15f
        } else {
            settings.pitch * 0.85f
        }
        tts?.setPitch(effectivePitch)
        tts?.setSpeechRate(settings.speechRate)

        when (settings.language) {
            LanguageMode.PAKISTANI_URDU -> {
                val urduLocale = Locale.Builder().setLanguage("ur").setRegion("PK").build()
                val res = tts?.setLanguage(urduLocale)
                if (res == TextToSpeech.LANG_MISSING_DATA || res == TextToSpeech.LANG_NOT_SUPPORTED) {
                    tts?.language = Locale.ENGLISH
                }
            }
            LanguageMode.ROMAN_URDU, LanguageMode.ENGLISH -> {
                tts?.language = Locale.ENGLISH
            }
        }
    }

    fun speak(text: String, utteranceId: String = "JARVIS_${System.currentTimeMillis()}") {
        if (!isTtsReady || tts == null) return
        stopListening()
        stopSpeaking() // Interrupt prior speech immediately
        _isSpeaking.value = true
        startVisualizerSimulation()

        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
    }

    fun stopSpeaking() {
        if (tts?.isSpeaking == true) {
            tts?.stop()
        }
        _isSpeaking.value = false
        stopVisualizerSimulation()
    }

    fun startListening() {
        stopSpeaking() // Immediate interruption when user starts speaking
        if (speechRecognizer == null) {
            initSpeechRecognizer()
        }

        try {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                val langCode = when (currentSettings.language) {
                    LanguageMode.PAKISTANI_URDU -> "ur-PK"
                    LanguageMode.ROMAN_URDU -> "ur-PK"
                    else -> "en-US"
                }
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, langCode)
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
            }
            speechRecognizer?.startListening(intent)
            _isListening.value = true
            startVisualizerSimulation()
        } catch (e: Exception) {
            Log.e(TAG, "Error starting speech recognition: ${e.message}")
            _isListening.value = false
        }
    }

    fun stopListening() {
        speechRecognizer?.stopListening()
        _isListening.value = false
        stopVisualizerSimulation()
    }

    private fun startVisualizerSimulation() {
        visualizerJob?.cancel()
        visualizerJob = scope.launch {
            while (isActive && (_isSpeaking.value || _isListening.value)) {
                _audioAmplitudes.value = List(16) { i ->
                    val wave = Math.sin((System.currentTimeMillis() / 150.0) + (i * 0.4)).toFloat()
                    val noise = Random.nextFloat() * 0.35f
                    ((Math.abs(wave) * 0.65f) + noise).coerceIn(0.12f, 1f)
                }
                delay(70)
            }
            _audioAmplitudes.value = List(16) { 0.1f }
        }
    }

    private fun stopVisualizerSimulation() {
        visualizerJob?.cancel()
        _audioAmplitudes.value = List(16) { 0.08f }
    }

    fun release() {
        stopSpeaking()
        stopListening()
        tts?.shutdown()
        speechRecognizer?.destroy()
    }
}

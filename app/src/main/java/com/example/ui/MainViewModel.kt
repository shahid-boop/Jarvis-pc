package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.db.AuditEntity
import com.example.data.db.ChatMessageEntity
import com.example.data.db.MemoryEntity
import com.example.data.model.AiProvider
import com.example.data.model.JarvisState
import com.example.data.model.LanguageMode
import com.example.data.model.VoiceGender
import com.example.data.model.VoiceSettings
import com.example.data.service.AiBrainRouter
import com.example.data.service.CodingStudioEngine
import com.example.data.service.JarvisAiResponse
import com.example.data.service.JarvisVoiceEngine
import com.example.data.service.PcAutomationManager
import com.example.data.service.WorkflowEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class JarvisScreen(val title: String, val icon: String) {
    HUD("Core HUD", "hud"),
    ATOZ_PC("A-Z PC Suite", "atoz"),
    CHAT("Voice & Chat", "chat"),
    PC_CONTROL("PC Operator", "pc"),
    WORKFLOWS("Workflows", "workflow"),
    CODING("Code Studio", "code"),
    FILES("Intelligence", "folder"),
    BRAIN("AI Router", "brain"),
    AUDIT("Memory & Logs", "history"),
    SETTINGS("Settings", "settings")
}

data class ConfirmationRequest(
    val title: String,
    val description: String,
    val onConfirm: () -> Unit
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val chatDao = db.chatDao()
    private val memoryDao = db.memoryDao()
    private val auditDao = db.auditDao()

    val voiceEngine = JarvisVoiceEngine(application)
    val aiRouter = AiBrainRouter()
    val pcManager = PcAutomationManager()
    val workflowEngine = WorkflowEngine()
    val codingEngine = CodingStudioEngine()

    val chatMessages: StateFlow<List<ChatMessageEntity>> = chatDao.getAllMessages()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val memoryItems: StateFlow<List<MemoryEntity>> = memoryDao.getAllMemories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val auditLogs: StateFlow<List<AuditEntity>> = auditDao.getAllAuditLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _currentScreen = MutableStateFlow(JarvisScreen.HUD)
    val currentScreen: StateFlow<JarvisScreen> = _currentScreen.asStateFlow()

    private val _jarvisState = MutableStateFlow(JarvisState.IDLE)
    val jarvisState: StateFlow<JarvisState> = _jarvisState.asStateFlow()

    private val _voiceSettings = MutableStateFlow(VoiceSettings())
    val voiceSettings: StateFlow<VoiceSettings> = _voiceSettings.asStateFlow()

    private val _selectedProvider = MutableStateFlow(AiProvider.GEMINI)
    val selectedProvider: StateFlow<AiProvider> = _selectedProvider.asStateFlow()

    private val _keyVault = MutableStateFlow(com.example.data.model.ApiKeyVault())
    val keyVault: StateFlow<com.example.data.model.ApiKeyVault> = _keyVault.asStateFlow()

    private val _customApiKey = MutableStateFlow("")
    val customApiKey: StateFlow<String> = _customApiKey.asStateFlow()

    private val _customEndpoint = MutableStateFlow("")
    val customEndpoint: StateFlow<String> = _customEndpoint.asStateFlow()

    private val _lastLatencyMs = MutableStateFlow<Long>(120)
    val lastLatencyMs: StateFlow<Long> = _lastLatencyMs.asStateFlow()

    private val _pendingConfirmation = MutableStateFlow<ConfirmationRequest?>(null)
    val pendingConfirmation: StateFlow<ConfirmationRequest?> = _pendingConfirmation.asStateFlow()

    private val _statusBannerText = MutableStateFlow("ALL SYSTEMS OPERATIONAL // تیار ہے")
    val statusBannerText: StateFlow<String> = _statusBannerText.asStateFlow()

    init {
        // Set up voice recognition callback
        voiceEngine.onSpeechRecognized = { spokenText ->
            handleUserInput(spokenText)
        }

        voiceEngine.onSpeechFinished = {
            if (_jarvisState.value == JarvisState.SPEAKING) {
                _jarvisState.value = JarvisState.IDLE
            }
        }

        // Initialize default memory items if empty
        viewModelScope.launch {
            val initial = listOf(
                MemoryEntity(key = "preferred_language", value = "Pakistani Urdu (اردو) & Roman Urdu", category = "preference"),
                MemoryEntity(key = "primary_pc", value = "JARVIS-DESKTOP (Windows 11 Pro)", category = "preference"),
                MemoryEntity(key = "free_coding_model", value = "Qwen 2.5 Coder 32B / DeepSeek R1 Free", category = "preference"),
                MemoryEntity(key = "shortcut_dev", value = "Open VS Code + Chrome + Terminal", category = "shortcut"),
                MemoryEntity(key = "active_project", value = "Python Automation & Voice Assistant", category = "project")
            )
            initial.forEach { memoryDao.insertMemory(it) }

            // Welcome message in Pakistani Urdu
            val recent = chatDao.getRecentMessages(1)
            if (recent.isEmpty()) {
                val welcome = ChatMessageEntity(
                    sender = "jarvis",
                    text = "اسلام علیکم سر! میں جاروس ہوں۔ تمام پی سی آٹومیشن سسٹمز، فری کوڈنگ ماڈلز اور وائس ریپٹرز آن لائن ہیں۔ آپ کے حکم کا منتظر ہوں۔",
                    language = "ur",
                    actionType = "system_init",
                    executionStatus = "success"
                )
                chatDao.insertMessage(welcome)
                auditDao.insertLog(
                    AuditEntity(
                        action = "System Startup",
                        target = "JARVIS Pakistani Core",
                        status = "Executed",
                        details = "Initialized A-Z PC controller, Pakistani Urdu voice, and Free Coding models matrix."
                    )
                )
            }
        }
    }

    fun navigateTo(screen: JarvisScreen) {
        _currentScreen.value = screen
    }

    fun setLanguage(lang: LanguageMode) {
        val updated = _voiceSettings.value.copy(language = lang)
        _voiceSettings.value = updated
        voiceEngine.applyVoiceSettings(updated)
    }

    fun setVoiceGender(gender: VoiceGender) {
        val updated = _voiceSettings.value.copy(gender = gender)
        _voiceSettings.value = updated
        voiceEngine.applyVoiceSettings(updated)
    }

    fun updateSpeechRate(rate: Float) {
        val updated = _voiceSettings.value.copy(speechRate = rate)
        _voiceSettings.value = updated
        voiceEngine.applyVoiceSettings(updated)
    }

    fun updatePitch(pitch: Float) {
        val updated = _voiceSettings.value.copy(pitch = pitch)
        _voiceSettings.value = updated
        voiceEngine.applyVoiceSettings(updated)
    }

    fun toggleWakeWord(enabled: Boolean) {
        _voiceSettings.value = _voiceSettings.value.copy(wakeWordEnabled = enabled)
    }

    fun setAiProvider(provider: AiProvider) {
        _selectedProvider.value = provider
    }

    fun updateGeminiKey(key: String) {
        _keyVault.value = _keyVault.value.copy(geminiKey = key)
        _customApiKey.value = key
    }

    fun updateGroqKey(key: String) {
        _keyVault.value = _keyVault.value.copy(groqKey = key)
    }

    fun updateOpenRouterKey(key: String) {
        _keyVault.value = _keyVault.value.copy(openRouterKey = key)
    }

    fun updateOpenAiKey(key: String) {
        _keyVault.value = _keyVault.value.copy(openAiKey = key)
    }

    fun updateClaudeKey(key: String) {
        _keyVault.value = _keyVault.value.copy(claudeKey = key)
    }

    fun updateOllamaEndpoint(url: String) {
        _keyVault.value = _keyVault.value.copy(ollamaEndpoint = url)
        _customEndpoint.value = url
    }

    fun executeAtoZ(feature: com.example.data.model.AtoZFeature) {
        if (feature.isDestructive) {
            _pendingConfirmation.value = ConfirmationRequest(
                title = "Security Gate: ${feature.title}",
                description = "Confirm execution of ${feature.title} (${feature.titleUrdu}) on Windows PC Workstation?",
                onConfirm = {
                    _pendingConfirmation.value = null
                    performAtoZExecution(feature)
                }
            )
            return
        }
        performAtoZExecution(feature)
    }

    private fun performAtoZExecution(feature: com.example.data.model.AtoZFeature) {
        viewModelScope.launch {
            _jarvisState.value = JarvisState.EXECUTING
            _statusBannerText.value = "A-Z [${feature.letter}]: ${feature.title} // ${feature.titleUrdu}"

            val resultMessage = pcManager.executeAtoZFeature(feature)

            val spokenText = when (_voiceSettings.value.language) {
                LanguageMode.PAKISTANI_URDU -> "${feature.titleUrdu} پر کامیابی سے عملدرآمد کر دیا گیا ہے، سر۔"
                LanguageMode.ROMAN_URDU -> "${feature.romanUrdu} execute ho gaya hai Sir. Status normal."
                LanguageMode.ENGLISH -> "${feature.title} executed successfully on Windows desktop, Sir."
            }

            val jarvisMsg = ChatMessageEntity(
                sender = "jarvis",
                text = "$spokenText\n$resultMessage",
                language = _voiceSettings.value.language.code,
                actionType = "atoz_${feature.letter.lowercase()}",
                executionStatus = "success"
            )
            chatDao.insertMessage(jarvisMsg)

            auditDao.insertLog(
                AuditEntity(
                    action = "A-Z: ${feature.title}",
                    target = "PC System [${feature.letter}]",
                    status = "Executed",
                    details = resultMessage
                )
            )

            _jarvisState.value = JarvisState.SPEAKING
            voiceEngine.speak(spokenText)
        }
    }

    fun setCustomApiKey(key: String) {
        _customApiKey.value = key
    }

    fun setCustomEndpoint(endpoint: String) {
        _customEndpoint.value = endpoint
    }

    fun startListening() {
        _jarvisState.value = JarvisState.LISTENING
        _statusBannerText.value = "AUDIO RECEPTORS LISTENING… // سن رہا ہوں"
        voiceEngine.startListening()
    }

    fun stopListening() {
        voiceEngine.stopListening()
        if (_jarvisState.value == JarvisState.LISTENING) {
            _jarvisState.value = JarvisState.IDLE
            _statusBannerText.value = "STANDBY // تیار ہے"
        }
    }

    fun interruptSpeech() {
        voiceEngine.stopSpeaking()
        _jarvisState.value = JarvisState.IDLE
        _statusBannerText.value = "SPEECH INTERRUPTED // خاموش"
    }

    fun handleUserInput(input: String) {
        if (input.isBlank()) return
        viewModelScope.launch {
            // Save user query
            val detectedLang = aiRouter.detectLanguage(input)
            val userMsg = ChatMessageEntity(
                sender = "user",
                text = input,
                language = detectedLang.code
            )
            chatDao.insertMessage(userMsg)

            _jarvisState.value = JarvisState.PROCESSING
            _statusBannerText.value = "NEURAL MATRIX EVALUATING COMMAND…"

            // Build context from recent messages
            val recent = chatDao.getRecentMessages(6).reversed().map { it.sender to it.text }

            val keyToUse = when (_selectedProvider.value) {
                AiProvider.GEMINI -> if (_keyVault.value.geminiKey.isNotBlank()) _keyVault.value.geminiKey else _customApiKey.value
                AiProvider.GROQ -> _keyVault.value.groqKey
                AiProvider.OPENROUTER_FREE -> _keyVault.value.openRouterKey
                AiProvider.OPENAI -> if (_keyVault.value.openAiKey.isNotBlank()) _keyVault.value.openAiKey else _customApiKey.value
                AiProvider.CLAUDE -> _keyVault.value.claudeKey
                else -> _customApiKey.value
            }
            val endpointToUse = if (_selectedProvider.value == AiProvider.OLLAMA) _keyVault.value.ollamaEndpoint else _customEndpoint.value

            val response: JarvisAiResponse = aiRouter.processQuery(
                userInput = input,
                selectedProvider = _selectedProvider.value,
                customApiKey = keyToUse,
                customEndpoint = endpointToUse,
                conversationContext = recent
            )

            _lastLatencyMs.value = response.latencyMs

            // Check if confirmation required (e.g., locking PC or deleting)
            if (response.requiresConfirmation) {
                _pendingConfirmation.value = ConfirmationRequest(
                    title = "Security Authorization Required",
                    description = response.confirmationPrompt ?: "Proceed with high-privilege action?",
                    onConfirm = {
                        executeConfirmedAction(response)
                    }
                )
                _jarvisState.value = JarvisState.SPEAKING
                _statusBannerText.value = "AWAITING CONFIRMATION: ${response.actionTarget}"
                voiceEngine.speak(response.replyText)
                return@launch
            }

            // Execute action if present
            executeActionDirectly(response)
        }
    }

    fun setPcVolume(level: Int) {
        viewModelScope.launch {
            pcManager.setVolume(level)
        }
    }

    fun launchPcApp(appName: String) {
        viewModelScope.launch {
            pcManager.launchApp(appName)
        }
    }

    private fun executeActionDirectly(response: JarvisAiResponse) {
        viewModelScope.launch {
            var actionStatus = "none"
            var targetDesc = response.actionTarget ?: "System"

            if (response.actionType != null) {
                _jarvisState.value = JarvisState.EXECUTING
                _statusBannerText.value = "EXECUTING: ${response.actionType} [${targetDesc}]"

                actionStatus = when (response.actionType) {
                    "pc_open_app" -> {
                        pcManager.launchApp(response.actionTarget ?: "Application")
                        "success"
                    }
                    "pc_search_files" -> {
                        navigateTo(JarvisScreen.FILES)
                        "success"
                    }
                    "code_gen" -> {
                        codingEngine.generateNewProject(response.actionTarget ?: "Python Script")
                        navigateTo(JarvisScreen.CODING)
                        "success"
                    }
                    "file_summary" -> {
                        navigateTo(JarvisScreen.FILES)
                        "success"
                    }
                    "pc_volume" -> {
                        pcManager.setVolume(response.actionTarget?.toIntOrNull() ?: 75)
                        "success"
                    }
                    else -> "success"
                }

                // Log audit
                auditDao.insertLog(
                    AuditEntity(
                        action = response.actionType,
                        target = targetDesc,
                        status = "Executed",
                        details = "Latency: ${response.latencyMs}ms | Provider: ${response.providerUsed.displayName}"
                    )
                )
            }

            // Save JARVIS message
            val jarvisMsg = ChatMessageEntity(
                sender = "jarvis",
                text = response.replyText,
                language = response.detectedLanguage.code,
                actionType = response.actionType,
                executionStatus = actionStatus
            )
            chatDao.insertMessage(jarvisMsg)

            // Speak response
            _jarvisState.value = JarvisState.SPEAKING
            _statusBannerText.value = "TRANSMITTING AUDIO SYNTHESIS…"
            voiceEngine.speak(response.replyText)
        }
    }

    private fun executeConfirmedAction(response: JarvisAiResponse) {
        _pendingConfirmation.value = null
        viewModelScope.launch {
            _jarvisState.value = JarvisState.EXECUTING
            if (response.actionType == "pc_lock") {
                pcManager.lockWorkstation()
            }
            auditDao.insertLog(
                AuditEntity(
                    action = response.actionType ?: "Action",
                    target = response.actionTarget ?: "Target",
                    status = "Confirmed & Executed",
                    details = "User explicitly confirmed action via authorization prompt."
                )
            )
            _statusBannerText.value = "ACTION CONFIRMED & EXECUTED"
            voiceEngine.speak("Authorization confirmed. Protocol executed successfully, Sir.")
        }
    }

    fun dismissConfirmation() {
        _pendingConfirmation.value = null
        _jarvisState.value = JarvisState.IDLE
        _statusBannerText.value = "ACTION ABORTED // STANDBY"
        viewModelScope.launch {
            auditDao.insertLog(
                AuditEntity(
                    action = "User Abort",
                    target = "Security Gate",
                    status = "Cancelled",
                    details = "User cancelled proposed action."
                )
            )
        }
    }

    fun clearChat() {
        viewModelScope.launch {
            chatDao.clearHistory()
        }
    }

    fun addMemory(key: String, value: String, category: String = "preference") {
        viewModelScope.launch {
            memoryDao.insertMemory(MemoryEntity(key = key, value = value, category = category))
        }
    }

    fun deleteMemory(memory: MemoryEntity) {
        viewModelScope.launch {
            memoryDao.deleteMemory(memory)
        }
    }

    override fun onCleared() {
        super.onCleared()
        voiceEngine.release()
    }
}

package com.example.data.model

enum class JarvisState(val label: String) {
    IDLE("STANDBY // تیار"),
    LISTENING("LISTENING // سن رہا ہوں"),
    PROCESSING("NEURAL PROCESSING // سوچ رہا ہوں"),
    SPEAKING("SPEAKING // بول رہا ہوں"),
    EXECUTING("EXECUTING // عملدرآمد جاری")
}

enum class LanguageMode(val displayName: String, val code: String, val greeting: String) {
    PAKISTANI_URDU(
        "اردو (پاکستان)",
        "ur",
        "اسلام علیکم سر! میں جاروس ہوں۔ بتائیں آپ کے کمپیوٹر کے لیے کیا حکم ہے؟"
    ),
    ROMAN_URDU(
        "Roman Urdu (PK)",
        "ur-roman",
        "Assalam-o-Alaikum Sir! JARVIS online hai. Aap ke PC ke liye kya hukam hai?"
    ),
    ENGLISH(
        "English",
        "en",
        "Good day, Sir. JARVIS is online and ready for PC automation."
    )
}

enum class VoiceGender(val label: String) {
    FEMALE("Female (Friday / Pakistani Female Voice)"),
    MALE("Male (JARVIS / Pakistani Male Voice)")
}

enum class AiProvider(
    val displayName: String,
    val defaultModel: String,
    val isFree: Boolean,
    val providerCategory: String, // "Free Cloud", "Free Local", "Commercial"
    val signupUrl: String
) {
    GEMINI("Google Gemini 3.5", "gemini-3.5-flash", true, "Free Cloud Tier", "https://aistudio.google.com/apikey"),
    GROQ("Groq Free LPU", "llama-3.3-70b-versatile", true, "100% Free Speed", "https://console.groq.com/keys"),
    OPENROUTER_FREE("OpenRouter Free Models", "deepseek/deepseek-r1:free", true, "100% Free Coding", "https://openrouter.ai/keys"),
    OLLAMA("Ollama Local AI", "qwen2.5-coder:latest", true, "Offline & Zero Cost", "http://localhost:11434"),
    OFFLINE("JARVIS Offline Pakistani Brain", "Neural-PK-Rules v3.5", true, "Offline Built-in", ""),
    OPENAI("OpenAI GPT", "gpt-4o-mini", false, "Commercial API", "https://platform.openai.com/api-keys"),
    CLAUDE("Anthropic Claude", "claude-3-5-sonnet-20241022", false, "Commercial API", "https://console.anthropic.com")
}

data class FreeCodingModel(
    val id: String,
    val name: String,
    val provider: String,
    val contextWindow: String,
    val isCompletelyFree: Boolean,
    val bestFor: String
)

data class AtoZFeature(
    val letter: Char,
    val code: String,
    val title: String,
    val titleUrdu: String,
    val romanUrdu: String,
    val description: String,
    val category: String,
    val iconName: String,
    val isDestructive: Boolean = false
)

data class VoiceSettings(
    val language: LanguageMode = LanguageMode.PAKISTANI_URDU,
    val gender: VoiceGender = VoiceGender.MALE,
    val speechRate: Float = 1.0f,
    val pitch: Float = 1.05f,
    val wakeWordEnabled: Boolean = true,
    val wakeWordSensitivity: Float = 0.8f,
    val continuousMode: Boolean = true
)

data class ApiKeyVault(
    val geminiKey: String = "",
    val groqKey: String = "",
    val openRouterKey: String = "",
    val openAiKey: String = "",
    val claudeKey: String = "",
    val ollamaEndpoint: String = "http://192.168.1.105:11434"
)

data class PcTelemetry(
    val connected: Boolean = true,
    val pcName: String = "JARVIS-DESKTOP",
    val ipAddress: String = "192.168.1.105:8765",
    val cpuUsage: Int = 19,
    val ramUsage: Int = 41,
    val batteryPercent: Int = 92,
    val activeWindow: String = "Visual Studio Code - main.py",
    val connectionMode: String = "Companion Bridge / WebSocket"
)

data class WorkflowStepItem(
    val id: Int,
    val title: String,
    val description: String,
    val toolName: String,
    val status: String = "pending",
    val output: String = ""
)

data class WorkflowPlan(
    val id: String,
    val title: String,
    val description: String,
    val steps: List<WorkflowStepItem>,
    val isRunning: Boolean = false,
    val completedCount: Int = 0
)

data class ProjectFile(
    val name: String,
    val content: String,
    val language: String
)

data class CodingProject(
    val id: String,
    val name: String,
    val description: String,
    val language: String,
    val files: List<ProjectFile>,
    val activeFileIndex: Int = 0,
    val terminalOutput: String = ""
)

data class PcApp(
    val name: String,
    val executable: String,
    val iconName: String,
    val category: String,
    val isRunning: Boolean = false
)

data class FileItem(
    val name: String,
    val path: String,
    val size: String,
    val type: String,
    val modifiedDate: String,
    val summary: String = ""
)

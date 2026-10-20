package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AiProvider
import com.example.ui.MainViewModel
import com.example.ui.components.HudGlassCard
import com.example.ui.theme.JarvisAmberWarning
import com.example.ui.theme.JarvisCyan
import com.example.ui.theme.JarvisCyanGlow
import com.example.ui.theme.JarvisDarkBg
import com.example.ui.theme.JarvisElectricBlue
import com.example.ui.theme.JarvisGreenSuccess
import com.example.ui.theme.JarvisHoloViolet
import com.example.ui.theme.JarvisHudBorder
import com.example.ui.theme.JarvisHudBorderActive
import com.example.ui.theme.JarvisSurfaceCard
import com.example.ui.theme.JarvisSurfaceCardElevated
import com.example.ui.theme.JarvisTextMuted
import com.example.ui.theme.JarvisTextPrimary
import com.example.ui.theme.JarvisTextSecondary
import kotlinx.coroutines.launch

@Composable
fun BrainRouterScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val selectedProvider by viewModel.selectedProvider.collectAsState()
    val keyVault by viewModel.keyVault.collectAsState()
    val latencyMs by viewModel.lastLatencyMs.collectAsState()
    val clipboardManager = LocalClipboardManager.current
    val coroutineScope = rememberCoroutineScope()

    var geminiKeyInput by remember { mutableStateOf(keyVault.geminiKey) }
    var groqKeyInput by remember { mutableStateOf(keyVault.groqKey) }
    var openRouterKeyInput by remember { mutableStateOf(keyVault.openRouterKey) }
    var openAiKeyInput by remember { mutableStateOf(keyVault.openAiKey) }
    var ollamaEndpointInput by remember { mutableStateOf(keyVault.ollamaEndpoint) }

    var showKeys by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(JarvisDarkBg)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "AI BRAIN & FREE MODELS ROUTER",
                        color = JarvisCyan,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(JarvisGreenSuccess.copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text("FREE TIER ACTIVE", color = JarvisGreenSuccess, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Text(
                    text = "Free coding models • Manual API keys vault • Pakistani Urdu engine",
                    color = JarvisTextMuted,
                    fontSize = 11.sp
                )
            }
        }

        // Active Router Telemetry Card
        HudGlassCard {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Hub,
                        contentDescription = null,
                        tint = JarvisCyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Active Engine: ${selectedProvider.displayName}",
                            color = JarvisTextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Model: ${selectedProvider.defaultModel}",
                            color = JarvisCyan,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (selectedProvider.isFree) JarvisGreenSuccess.copy(alpha = 0.15f) else JarvisAmberWarning.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (selectedProvider.isFree) "100% FREE" else "COMMERCIAL",
                        color = if (selectedProvider.isFree) JarvisGreenSuccess else JarvisAmberWarning,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Speed,
                        contentDescription = null,
                        tint = JarvisTextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Benchmark Latency: ${latencyMs}ms",
                        color = JarvisTextSecondary,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Button(
                    onClick = {
                        coroutineScope.launch {
                            viewModel.handleUserInput("JARVIS latency ping test")
                            statusMessage = "Latency ping test dispatched to ${selectedProvider.displayName}."
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = JarvisSurfaceCardElevated),
                    border = BorderStroke(1.dp, JarvisCyan.copy(alpha = 0.4f)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Ping Model", color = JarvisCyan, fontSize = 10.sp)
                }
            }
        }

        // Section: Select AI Engine
        Text(
            text = "SELECT AI PROVIDER (FREE MODELS HIGHLIGHTED)",
            color = JarvisTextMuted,
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 1.sp
        )

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            AiProvider.values().forEach { provider ->
                val isSelected = selectedProvider == provider
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isSelected) JarvisSurfaceCardElevated else JarvisSurfaceCard,
                    border = BorderStroke(1.dp, if (isSelected) JarvisCyan else JarvisHudBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.setAiProvider(provider)
                            statusMessage = "Switched to ${provider.displayName} (${provider.defaultModel})"
                        }
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .background(if (isSelected) JarvisCyan else JarvisHudBorder, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = provider.displayName,
                                        color = if (isSelected) JarvisCyanGlow else JarvisTextPrimary,
                                        fontSize = 13.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold
                                    )
                                    if (provider.isFree) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Box(
                                            modifier = Modifier
                                                .background(JarvisGreenSuccess.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                                .padding(horizontal = 4.dp, vertical = 2.dp)
                                        ) {
                                            Text("FREE", color = JarvisGreenSuccess, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                                Text(
                                    text = "Model: ${provider.defaultModel} • ${provider.providerCategory}",
                                    color = JarvisTextMuted,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = JarvisCyan,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }

        // Section: Manual API Key Vault ("key add manually")
        HudGlassCard {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Key,
                        contentDescription = null,
                        tint = JarvisCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "MANUAL API KEYS VAULT",
                        color = JarvisCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                IconButton(
                    onClick = { showKeys = !showKeys },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (showKeys) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = "Toggle Key Mask",
                        tint = JarvisTextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Add your free or custom API keys manually here. They are stored locally on your device in secure sandbox storage.",
                color = JarvisTextSecondary,
                fontSize = 11.sp,
                lineHeight = 15.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 1. Google Gemini Key Input
            KeyInputField(
                label = "Google Gemini API Key (Free Tier)",
                value = geminiKeyInput,
                onValueChange = { geminiKeyInput = it },
                showKey = showKeys,
                placeholder = "AIzaSy...",
                helperUrl = "https://aistudio.google.com/apikey",
                onCopyUrl = {
                    clipboardManager.setText(AnnotatedString("https://aistudio.google.com/apikey"))
                    statusMessage = "Copied Google AI Studio key URL to clipboard."
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 2. Groq Free LPU Key Input
            KeyInputField(
                label = "Groq API Key (100% Free Speed)",
                value = groqKeyInput,
                onValueChange = { groqKeyInput = it },
                showKey = showKeys,
                placeholder = "gsk_...",
                helperUrl = "https://console.groq.com/keys",
                onCopyUrl = {
                    clipboardManager.setText(AnnotatedString("https://console.groq.com/keys"))
                    statusMessage = "Copied Groq Console URL to clipboard."
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 3. OpenRouter Free Models Key Input
            KeyInputField(
                label = "OpenRouter API Key (DeepSeek R1 / Qwen Free)",
                value = openRouterKeyInput,
                onValueChange = { openRouterKeyInput = it },
                showKey = showKeys,
                placeholder = "sk-or-v1-...",
                helperUrl = "https://openrouter.ai/keys",
                onCopyUrl = {
                    clipboardManager.setText(AnnotatedString("https://openrouter.ai/keys"))
                    statusMessage = "Copied OpenRouter URL to clipboard."
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 4. Ollama Local PC Endpoint
            OutlinedTextField(
                value = ollamaEndpointInput,
                onValueChange = { ollamaEndpointInput = it },
                label = { Text("Ollama Local Endpoint (Zero Key Required)", fontSize = 11.sp) },
                placeholder = { Text("http://192.168.1.105:11434", color = JarvisTextMuted, fontSize = 11.sp) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = JarvisDarkBg,
                    unfocusedContainerColor = JarvisDarkBg,
                    focusedTextColor = JarvisTextPrimary,
                    unfocusedTextColor = JarvisTextPrimary,
                    cursorColor = JarvisCyan,
                    focusedIndicatorColor = JarvisCyan,
                    unfocusedIndicatorColor = JarvisHudBorder
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Save All Keys Button
            Button(
                onClick = {
                    viewModel.updateGeminiKey(geminiKeyInput.trim())
                    viewModel.updateGroqKey(groqKeyInput.trim())
                    viewModel.updateOpenRouterKey(openRouterKeyInput.trim())
                    viewModel.updateOpenAiKey(openAiKeyInput.trim())
                    viewModel.updateOllamaEndpoint(ollamaEndpointInput.trim())
                    statusMessage = "All manual API keys saved to secure local vault!"
                },
                colors = ButtonDefaults.buttonColors(containerColor = JarvisCyan),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Key,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Save Manual API Keys to Vault", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }

        if (statusMessage.isNotBlank()) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = JarvisSurfaceCard,
                border = BorderStroke(1.dp, JarvisCyanGlow),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = statusMessage,
                    color = JarvisCyanGlow,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(10.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun KeyInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    showKey: Boolean,
    placeholder: String,
    helperUrl: String,
    onCopyUrl: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = label, color = JarvisTextPrimary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            Row(
                modifier = Modifier.clickable { onCopyUrl() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Get Free Key", color = JarvisCyan, fontSize = 10.sp)
                Spacer(modifier = Modifier.width(2.dp))
                Icon(imageVector = Icons.Default.OpenInNew, contentDescription = null, tint = JarvisCyan, modifier = Modifier.size(10.dp))
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = JarvisTextMuted, fontSize = 11.sp) },
            visualTransformation = if (showKey) VisualTransformation.None else PasswordVisualTransformation(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = JarvisDarkBg,
                unfocusedContainerColor = JarvisDarkBg,
                focusedTextColor = JarvisTextPrimary,
                unfocusedTextColor = JarvisTextPrimary,
                cursorColor = JarvisCyan,
                focusedIndicatorColor = JarvisCyan,
                unfocusedIndicatorColor = JarvisHudBorder
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
    }
}

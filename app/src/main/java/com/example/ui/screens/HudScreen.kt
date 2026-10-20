package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.JarvisState
import com.example.data.model.LanguageMode
import com.example.ui.JarvisScreen
import com.example.ui.MainViewModel
import com.example.ui.components.HudGlassCard
import com.example.ui.components.HudTelemetryHeader
import com.example.ui.components.JarvisOrb
import com.example.ui.components.WaveformVisualizer
import com.example.ui.theme.JarvisAmberWarning
import com.example.ui.theme.JarvisCyan
import com.example.ui.theme.JarvisCyanGlow
import com.example.ui.theme.JarvisDarkBg
import com.example.ui.theme.JarvisElectricBlue
import com.example.ui.theme.JarvisHudBorder
import com.example.ui.theme.JarvisHudBorderActive
import com.example.ui.theme.JarvisRedAlert
import com.example.ui.theme.JarvisSurfaceCard
import com.example.ui.theme.JarvisTextMuted
import com.example.ui.theme.JarvisTextPrimary
import com.example.ui.theme.JarvisTextSecondary

@Composable
fun HudScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.jarvisState.collectAsState()
    val telemetry by viewModel.pcManager.telemetry.collectAsState()
    val amplitudes by viewModel.voiceEngine.audioAmplitudes.collectAsState()
    val voiceSettings by viewModel.voiceSettings.collectAsState()
    val latencyMs by viewModel.lastLatencyMs.collectAsState()
    val statusBanner by viewModel.statusBannerText.collectAsState()
    val isSpeaking by viewModel.voiceEngine.isSpeaking.collectAsState()
    val isListening by viewModel.voiceEngine.isListening.collectAsState()
    val volume by viewModel.pcManager.systemVolume.collectAsState()

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(JarvisDarkBg)
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Telemetry status strip
        HudTelemetryHeader(
            telemetry = telemetry,
            state = state,
            latencyMs = latencyMs,
            currentLanguage = voiceSettings.language
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Language Mode Quick Toggle Pill
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(JarvisSurfaceCard)
                .border(1.dp, JarvisHudBorder, RoundedCornerShape(20.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            LanguageMode.values().forEach { mode ->
                val selected = voiceSettings.language == mode
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (selected) JarvisCyan else Color.Transparent)
                        .clickable { viewModel.setLanguage(mode) }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = mode.displayName,
                        color = if (selected) Color.Black else JarvisTextSecondary,
                        fontSize = 11.sp,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Center 3D-style Holographic Orb with audio reactive pulse
        JarvisOrb(
            state = state,
            amplitudes = amplitudes,
            size = 230.dp,
            onClick = {
                if (isListening) viewModel.stopListening() else viewModel.startListening()
            },
            modifier = Modifier.testTag("jarvis_central_orb")
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Status Label & State Badge
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = JarvisSurfaceCard,
            border = BorderStroke(1.dp, JarvisHudBorderActive)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(
                            when (state) {
                                JarvisState.IDLE -> JarvisCyan
                                JarvisState.LISTENING -> JarvisCyanGlow
                                JarvisState.PROCESSING -> JarvisAmberWarning
                                JarvisState.SPEAKING -> JarvisElectricBlue
                                JarvisState.EXECUTING -> JarvisCyanGlow
                            },
                            CircleShape
                        )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = statusBanner,
                    color = JarvisTextPrimary,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Real-time audio waveform visualizer
        WaveformVisualizer(
            amplitudes = amplitudes,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Voice Controls Row: Push-to-Talk, Interrupt/Stop, Wake Word status
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Main Mic Button
            Button(
                onClick = {
                    if (isListening) viewModel.stopListening() else viewModel.startListening()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isListening) JarvisRedAlert else JarvisCyan
                ),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .height(48.dp)
                    .testTag("voice_mic_button")
            ) {
                Icon(
                    imageVector = if (isListening) Icons.Default.MicOff else Icons.Default.Mic,
                    contentDescription = "Voice Input",
                    tint = Color.Black
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isListening) "Listening… (Tap to Stop)" else "Speak to JARVIS",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Immediate Interrupt / Silence Speech Button
            if (isSpeaking) {
                IconButton(
                    onClick = { viewModel.interruptSpeech() },
                    modifier = Modifier
                        .size(48.dp)
                        .background(JarvisRedAlert.copy(alpha = 0.2f), CircleShape)
                        .border(1.dp, JarvisRedAlert, CircleShape)
                        .testTag("interrupt_speech_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Stop,
                        contentDescription = "Interrupt Speech",
                        tint = JarvisRedAlert
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Quick Verbal Prompt Chips
        Text(
            text = "VERBAL COMMAND PROTOCOLS (TAP TO EXECUTE)",
            color = JarvisTextMuted,
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 1.sp,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val sampleCommands = listOf(
                "اسلام علیکم جاروس",
                "Jarvis, Chrome kholo",
                "میرے ڈاؤن لوڈز میں پی ڈی ایف ڈھونڈو",
                "ریسائیکل بن خالی کرو",
                "اسکرین کا اردو خلاصہ بتاؤ",
                "پائیتھن میں کیلکولیٹر بناؤ",
                "Lock workstation",
                "Battery kitni hai",
                "Volume 80% karo"
            )

            sampleCommands.forEach { cmd ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(JarvisSurfaceCard)
                        .border(1.dp, JarvisHudBorder, RoundedCornerShape(12.dp))
                        .clickable { viewModel.handleUserInput(cmd) }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "“$cmd”",
                        color = JarvisCyanGlow,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Active PC Telemetry Card
        HudGlassCard {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Computer,
                        contentDescription = null,
                        tint = JarvisCyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Active PC Session: ${telemetry.pcName}",
                        color = JarvisTextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = telemetry.ipAddress,
                    color = JarvisTextMuted,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Focused Window: ${telemetry.activeWindow}",
                color = JarvisTextSecondary,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.VolumeUp,
                    contentDescription = null,
                    tint = JarvisTextSecondary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "PC Volume: $volume%",
                    color = JarvisTextSecondary,
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Slider(
                    value = volume.toFloat(),
                    onValueChange = { viewModel.setPcVolume(it.toInt()) },
                    valueRange = 0f..100f,
                    colors = SliderDefaults.colors(
                        thumbColor = JarvisCyan,
                        activeTrackColor = JarvisCyan,
                        inactiveTrackColor = JarvisHudBorder
                    ),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Button(
                    onClick = { viewModel.navigateTo(JarvisScreen.ATOZ_PC) },
                    colors = ButtonDefaults.buttonColors(containerColor = JarvisCyan),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("A-Z PC Suite", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }

                Button(
                    onClick = { viewModel.navigateTo(JarvisScreen.PC_CONTROL) },
                    colors = ButtonDefaults.buttonColors(containerColor = JarvisSurfaceCard),
                    border = BorderStroke(1.dp, JarvisCyan.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("PC Control", color = JarvisCyan, fontSize = 11.sp)
                }

                Button(
                    onClick = { viewModel.navigateTo(JarvisScreen.WORKFLOWS) },
                    colors = ButtonDefaults.buttonColors(containerColor = JarvisSurfaceCard),
                    border = BorderStroke(1.dp, JarvisElectricBlue.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Workflows", color = JarvisElectricBlue, fontSize = 11.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

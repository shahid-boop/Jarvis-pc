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
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LanguageMode
import com.example.data.model.VoiceGender
import com.example.ui.MainViewModel
import com.example.ui.components.HudGlassCard
import com.example.ui.theme.JarvisCyan
import com.example.ui.theme.JarvisCyanGlow
import com.example.ui.theme.JarvisDarkBg
import com.example.ui.theme.JarvisElectricBlue
import com.example.ui.theme.JarvisGreenSuccess
import com.example.ui.theme.JarvisHudBorder
import com.example.ui.theme.JarvisHudBorderActive
import com.example.ui.theme.JarvisSurfaceCard
import com.example.ui.theme.JarvisSurfaceCardElevated
import com.example.ui.theme.JarvisTextMuted
import com.example.ui.theme.JarvisTextPrimary
import com.example.ui.theme.JarvisTextSecondary

@Composable
fun SettingsScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val voiceSettings by viewModel.voiceSettings.collectAsState()
    val telemetry by viewModel.pcManager.telemetry.collectAsState()
    val clipboardManager = LocalClipboardManager.current

    var pcIpInput by remember { mutableStateOf(telemetry.ipAddress) }
    var noticeMessage by remember { mutableStateOf("") }

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
                Text(
                    text = "JARVIS SYSTEM PROTOCOLS & CONFIG",
                    color = JarvisCyan,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "Acoustic synthesis • Wake word • PC Companion Bridge configuration",
                    color = JarvisTextMuted,
                    fontSize = 11.sp
                )
            }
        }

        // 1. Voice Personality & Gender
        HudGlassCard {
            Text(
                text = "VOICE ENGINE & PERSONALITY PROFILE",
                color = JarvisTextMuted,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                VoiceGender.values().forEach { gender ->
                    val isSelected = voiceSettings.gender == gender
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) JarvisSurfaceCardElevated else JarvisSurfaceCard,
                        border = BorderStroke(1.dp, if (isSelected) JarvisCyan else JarvisHudBorder),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { viewModel.setVoiceGender(gender) }
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.RecordVoiceOver,
                                contentDescription = null,
                                tint = if (isSelected) JarvisCyan else JarvisTextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = gender.name,
                                color = if (isSelected) JarvisCyanGlow else JarvisTextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (gender == VoiceGender.FEMALE) "Crisp / Clear" else "Deep / Resonant",
                                color = JarvisTextMuted,
                                fontSize = 9.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Speech Rate Slider
            Text(
                text = "Speech Rate: ${String.format("%.2f", voiceSettings.speechRate)}x",
                color = JarvisTextSecondary,
                fontSize = 11.sp
            )
            Slider(
                value = voiceSettings.speechRate,
                onValueChange = { viewModel.updateSpeechRate(it) },
                valueRange = 0.75f..1.4f,
                colors = SliderDefaults.colors(
                    thumbColor = JarvisCyan,
                    activeTrackColor = JarvisCyan,
                    inactiveTrackColor = JarvisHudBorder
                )
            )

            // Pitch Slider
            Text(
                text = "Voice Pitch Frequency: ${String.format("%.2f", voiceSettings.pitch)}",
                color = JarvisTextSecondary,
                fontSize = 11.sp
            )
            Slider(
                value = voiceSettings.pitch,
                onValueChange = { viewModel.updatePitch(it) },
                valueRange = 0.7f..1.5f,
                colors = SliderDefaults.colors(
                    thumbColor = JarvisElectricBlue,
                    activeTrackColor = JarvisElectricBlue,
                    inactiveTrackColor = JarvisHudBorder
                )
            )

            // Test Voice Button
            Button(
                onClick = {
                    val phrase = when (voiceSettings.language) {
                        LanguageMode.PAKISTANI_URDU -> "اسلام علیکم سر، جاروس کے تمام صوتی سسٹمز اور پاکستانی اردو پروٹوکولز بالکل ٹھیک کام کر رہے ہیں۔"
                        LanguageMode.ROMAN_URDU -> "Assalam-o-Alaikum Sir! Voice synthesis testing complete. Systems are running smoothly."
                        LanguageMode.ENGLISH -> "Voice synthesis test confirmed. All neural acoustic filters are operational, Sir."
                    }
                    viewModel.voiceEngine.speak(phrase)
                },
                colors = ButtonDefaults.buttonColors(containerColor = JarvisSurfaceCardElevated),
                border = BorderStroke(1.dp, JarvisCyan.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(imageVector = Icons.Default.VolumeUp, contentDescription = null, tint = JarvisCyan, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Test Voice Synthesis (اردو / English)", color = JarvisCyan, fontSize = 11.sp)
            }
        }

        // 2. Wake Word & Continuous Listening
        HudGlassCard {
            Text(
                text = "WAKE WORD & MICROPHONE ACTIVATION",
                color = JarvisTextMuted,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Wake Word: “Hey JARVIS”",
                        color = JarvisTextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Activates acoustic listener when phrase is spoken",
                        color = JarvisTextMuted,
                        fontSize = 10.sp
                    )
                }

                Switch(
                    checked = voiceSettings.wakeWordEnabled,
                    onCheckedChange = { viewModel.toggleWakeWord(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.Black,
                        checkedTrackColor = JarvisCyan,
                        uncheckedTrackColor = JarvisHudBorder
                    )
                )
            }
        }

        // 3. PC Companion Bridge Network Settings
        HudGlassCard {
            Text(
                text = "WINDOWS PC COMPANION BRIDGE CONNECTION",
                color = JarvisTextMuted,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = pcIpInput,
                onValueChange = { pcIpInput = it },
                label = { Text("PC LAN IP & Port", fontSize = 11.sp) },
                placeholder = { Text("192.168.1.105:8765", color = JarvisTextMuted, fontSize = 11.sp) },
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

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        viewModel.pcManager.updateTelemetry(pcIpInput.trim())
                        noticeMessage = "PC Bridge IP address updated to $pcIpInput"
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = JarvisCyan),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Save Bridge IP", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }

                Button(
                    onClick = {
                        val script = viewModel.pcManager.getPythonServerScript()
                        clipboardManager.setText(AnnotatedString(script))
                        noticeMessage = "Copied jarvis_pc_server.py to clipboard!"
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = JarvisSurfaceCardElevated),
                    border = BorderStroke(1.dp, JarvisElectricBlue.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(imageVector = Icons.Default.Code, contentDescription = null, tint = JarvisElectricBlue, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Export Server", color = JarvisElectricBlue, fontSize = 11.sp)
                }
            }

            if (noticeMessage.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = noticeMessage,
                    color = JarvisCyanGlow,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

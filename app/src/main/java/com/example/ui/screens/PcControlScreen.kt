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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Web
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.ConfirmationRequest
import com.example.ui.MainViewModel
import com.example.ui.components.HudGlassCard
import com.example.ui.theme.JarvisAmberWarning
import com.example.ui.theme.JarvisCyan
import com.example.ui.theme.JarvisCyanGlow
import com.example.ui.theme.JarvisDarkBg
import com.example.ui.theme.JarvisElectricBlue
import com.example.ui.theme.JarvisGreenSuccess
import com.example.ui.theme.JarvisHudBorder
import com.example.ui.theme.JarvisHudBorderActive
import com.example.ui.theme.JarvisRedAlert
import com.example.ui.theme.JarvisSurfaceCard
import com.example.ui.theme.JarvisSurfaceCardElevated
import com.example.ui.theme.JarvisTextMuted
import com.example.ui.theme.JarvisTextPrimary
import com.example.ui.theme.JarvisTextSecondary
import kotlinx.coroutines.launch

@Composable
fun PcControlScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val telemetry by viewModel.pcManager.telemetry.collectAsState()
    val processes by viewModel.pcManager.runningProcesses.collectAsState()
    val volume by viewModel.pcManager.systemVolume.collectAsState()
    val isMuted by viewModel.pcManager.isMuted.collectAsState()
    val isLocked by viewModel.pcManager.isLocked.collectAsState()
    val screenOcr by viewModel.pcManager.screenOcrText.collectAsState()
    val clipboardManager = LocalClipboardManager.current
    val coroutineScope = rememberCoroutineScope()

    var terminalCommand by remember { mutableStateOf("dir") }
    var terminalOutput by remember { mutableStateOf("") }
    var statusNotice by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(JarvisDarkBg)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Section Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "WINDOWS PC OPERATOR DECK",
                    color = JarvisCyan,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "Desktop automation • Process control • Terminal agent",
                    color = JarvisTextMuted,
                    fontSize = 11.sp
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (telemetry.connected) JarvisGreenSuccess.copy(alpha = 0.15f) else JarvisRedAlert.copy(alpha = 0.15f))
                    .border(1.dp, if (telemetry.connected) JarvisGreenSuccess else JarvisRedAlert, RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = if (telemetry.connected) "CONNECTED" else "OFFLINE",
                    color = if (telemetry.connected) JarvisGreenSuccess else JarvisRedAlert,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Live Notice Bar if any
        if (statusNotice.isNotBlank()) {
            Surface(
                color = JarvisSurfaceCard,
                border = BorderStroke(1.dp, JarvisCyan.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = statusNotice,
                    color = JarvisCyanGlow,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(10.dp)
                )
            }
        }

        // 1. Application Launcher Grid
        HudGlassCard {
            Text(
                text = "APPLICATION LAUNCH MATRIX",
                color = JarvisTextMuted,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                val apps = viewModel.pcManager.availableApps
                apps.chunked(2).forEach { rowApps ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowApps.forEach { app ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = JarvisSurfaceCardElevated,
                                border = BorderStroke(1.dp, if (app.isRunning) JarvisCyanGlow.copy(alpha = 0.4f) else JarvisHudBorder),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        coroutineScope.launch {
                                            statusNotice = viewModel.pcManager.launchApp(app.name)
                                        }
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = when (app.iconName) {
                                            "web" -> Icons.Default.Web
                                            "terminal" -> Icons.Default.Terminal
                                            "folder" -> Icons.Default.Folder
                                            else -> Icons.Default.Computer
                                        },
                                        contentDescription = null,
                                        tint = if (app.isRunning) JarvisCyan else JarvisTextSecondary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = app.name,
                                            color = JarvisTextPrimary,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            text = if (app.isRunning) "Running" else "Launch",
                                            color = if (app.isRunning) JarvisGreenSuccess else JarvisTextMuted,
                                            fontSize = 9.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 2. Hardware & Security Controls (Volume, Mute, Lock, Display)
        HudGlassCard {
            Text(
                text = "HARDWARE & SECURITY CONTROLS",
                color = JarvisTextMuted,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(10.dp))

            // Volume Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { coroutineScope.launch { statusNotice = viewModel.pcManager.toggleMute() } },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = if (isMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                        contentDescription = "Toggle Mute",
                        tint = if (isMuted) JarvisRedAlert else JarvisCyan
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                Slider(
                    value = volume.toFloat(),
                    onValueChange = { coroutineScope.launch { viewModel.pcManager.setVolume(it.toInt()) } },
                    valueRange = 0f..100f,
                    colors = SliderDefaults.colors(
                        thumbColor = JarvisCyan,
                        activeTrackColor = JarvisCyan,
                        inactiveTrackColor = JarvisHudBorder
                    ),
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "$volume%",
                    color = JarvisTextPrimary,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Quick Security Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Lock PC Button with Confirmation Gate
                Button(
                    onClick = {
                        if (isLocked) {
                            coroutineScope.launch { statusNotice = viewModel.pcManager.unlockWorkstation() }
                        } else {
                            viewModel.handleUserInput("Lock workstation")
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isLocked) JarvisGreenSuccess.copy(alpha = 0.2f) else JarvisAmberWarning.copy(alpha = 0.2f)
                    ),
                    border = BorderStroke(1.dp, if (isLocked) JarvisGreenSuccess else JarvisAmberWarning),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = if (isLocked) JarvisGreenSuccess else JarvisAmberWarning,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isLocked) "Unlock PC" else "Lock Workstation",
                        color = if (isLocked) JarvisGreenSuccess else JarvisAmberWarning,
                        fontSize = 11.sp
                    )
                }

                // OCR Screen Analysis
                Button(
                    onClick = {
                        coroutineScope.launch {
                            statusNotice = "Screen OCR complete: Active window text analyzed."
                            viewModel.pcManager.scanScreenOcr()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = JarvisSurfaceCardElevated),
                    border = BorderStroke(1.dp, JarvisCyan.copy(alpha = 0.4f)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.DocumentScanner,
                        contentDescription = null,
                        tint = JarvisCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("OCR Screen", color = JarvisCyan, fontSize = 11.sp)
                }
            }
        }

        // 3. Screen OCR & Visual Inspector
        HudGlassCard {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ACTIVE DESKTOP VISION & OCR",
                    color = JarvisTextMuted,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )
                IconButton(
                    onClick = { coroutineScope.launch { viewModel.pcManager.scanScreenOcr() } },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Rescan",
                        tint = JarvisCyan,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Surface(
                color = JarvisDarkBg,
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, JarvisHudBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = screenOcr,
                    color = JarvisCyanGlow,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    lineHeight = 16.sp,
                    modifier = Modifier.padding(10.dp)
                )
            }
        }

        // 4. Live Process Manager
        HudGlassCard {
            Text(
                text = "RUNNING PC PROCESSES (${processes.size})",
                color = JarvisTextMuted,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                processes.forEach { proc ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(JarvisSurfaceCardElevated)
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = proc.name,
                                color = JarvisTextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "PID: ${proc.pid} • CPU: ${proc.cpuPercent}% • RAM: ${proc.memoryMb} MB",
                                color = JarvisTextMuted,
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    statusNotice = viewModel.pcManager.terminateProcess(proc.pid)
                                }
                            },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Terminate Process",
                                tint = JarvisRedAlert,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }

        // 5. PowerShell & Terminal Execution Console
        HudGlassCard {
            Text(
                text = "POWERSHELL / BASH TERMINAL EXECUTOR",
                color = JarvisTextMuted,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = terminalCommand,
                    onValueChange = { terminalCommand = it },
                    placeholder = { Text("Enter command (e.g., dir, python --version, git status)", color = JarvisTextMuted, fontSize = 11.sp) },
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
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        coroutineScope.launch {
                            terminalOutput = viewModel.pcManager.executeTerminalCommand(terminalCommand)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = JarvisElectricBlue),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Run", color = Color.White, fontSize = 11.sp)
                }
            }

            if (terminalOutput.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = JarvisDarkBg,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, JarvisHudBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = terminalOutput,
                        color = JarvisCyan,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }
        }

        // 6. Python Companion Script Exporter
        HudGlassCard {
            Text(
                text = "PYTHON PC COMPANION SERVER",
                color = JarvisTextMuted,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Run this FastAPI/WebSocket server on your Windows machine to allow JARVIS to directly automate applications, mouse, keyboard, and files over LAN.",
                color = JarvisTextSecondary,
                fontSize = 11.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    val script = viewModel.pcManager.getPythonServerScript()
                    clipboardManager.setText(AnnotatedString(script))
                    statusNotice = "Python PC Server script copied to clipboard! Save as jarvis_pc_server.py on your PC."
                },
                colors = ButtonDefaults.buttonColors(containerColor = JarvisSurfaceCardElevated),
                border = BorderStroke(1.dp, JarvisCyan),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Code,
                    contentDescription = null,
                    tint = JarvisCyan,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Copy PC Companion Script (Python)", color = JarvisCyan, fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

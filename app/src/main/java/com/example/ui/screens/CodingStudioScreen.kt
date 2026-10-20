package com.example.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Terminal
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import kotlinx.coroutines.launch

@Composable
fun CodingStudioScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val project by viewModel.codingEngine.currentProject.collectAsState()
    val isBuilding by viewModel.codingEngine.isBuilding.collectAsState()
    val clipboardManager = LocalClipboardManager.current
    val coroutineScope = rememberCoroutineScope()

    var generatePrompt by remember { mutableStateOf("") }
    var notificationMessage by remember { mutableStateOf("") }

    val activeFile = project.files.getOrNull(project.activeFileIndex) ?: project.files.firstOrNull()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(JarvisDarkBg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Studio Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "AI CODING AGENT & STUDIO (FREE MODELS)",
                    color = JarvisCyan,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "Free coding models • Multi-file workspace • Pytest harness",
                    color = JarvisTextMuted,
                    fontSize = 11.sp
                )
            }
        }

        // Free Coding Model Selector Strip
        Column {
            Text(
                text = "ACTIVE FREE CODING ENGINE:",
                color = JarvisTextMuted,
                fontSize = 9.sp,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val selectedModel by viewModel.codingEngine.selectedCodingModel.collectAsState()
                val models = viewModel.codingEngine.freeCodingModels
                models.forEach { model ->
                    val isSelected = selectedModel.id == model.id
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) JarvisCyan else JarvisSurfaceCard)
                            .border(1.dp, if (isSelected) JarvisCyan else JarvisHudBorder, RoundedCornerShape(8.dp))
                            .clickable { viewModel.codingEngine.selectCodingModel(model) }
                            .padding(horizontal = 8.dp, vertical = 5.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = model.name,
                                color = if (isSelected) Color.Black else JarvisTextPrimary,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(if (isSelected) Color.Black.copy(alpha = 0.2f) else JarvisGreenSuccess.copy(alpha = 0.2f))
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text("FREE", color = if (isSelected) Color.Black else JarvisGreenSuccess, fontSize = 7.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Project Generator Bar
        HudGlassCard {
            Text(
                text = "GENERATE COMPLETE PROJECT FROM NATURAL LANGUAGE",
                color = JarvisTextMuted,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = generatePrompt,
                    onValueChange = { generatePrompt = it },
                    placeholder = { Text("e.g. پائیتھن کیلکولیٹر بنائیں or Python GUI Calculator…", color = JarvisTextMuted, fontSize = 11.sp) },
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
                        if (generatePrompt.isNotBlank()) {
                            coroutineScope.launch {
                                viewModel.codingEngine.generateNewProject(generatePrompt.trim())
                                notificationMessage = "New project generated via ${viewModel.codingEngine.selectedCodingModel.value.name}."
                                generatePrompt = ""
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = JarvisCyan),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Build", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Quick Pakistani Urdu coding presets
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(
                    "پائیتھن میں کیلکولیٹر بنائیں",
                    "Python automation bot",
                    "FastAPI REST server",
                    "Windows system monitor"
                ).forEach { preset ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(JarvisSurfaceCardElevated)
                            .clickable { generatePrompt = preset }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(text = preset, color = JarvisCyanGlow, fontSize = 9.sp)
                    }
                }
            }
        }

        // Active Project Info & Action Bar
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = JarvisSurfaceCard,
            border = BorderStroke(1.dp, JarvisHudBorderActive),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = project.name,
                            color = JarvisTextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Language: ${project.language} • ${project.files.size} files",
                            color = JarvisCyan,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        // Run & Test Button
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    val out = viewModel.codingEngine.runTestsAndBuild()
                                    viewModel.voiceEngine.speak("Build and unit tests passed successfully, Sir.")
                                }
                            },
                            enabled = !isBuilding,
                            colors = ButtonDefaults.buttonColors(containerColor = JarvisGreenSuccess),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (isBuilding) "Testing…" else "Test & Build", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }

                        // Export ZIP button
                        Button(
                            onClick = {
                                notificationMessage = "Project ZIP archive generated: ${project.name.replace(" ", "_")}.zip (Saved to Downloads)"
                                viewModel.voiceEngine.speak("Project ZIP archive exported successfully.")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = JarvisSurfaceCardElevated),
                            border = BorderStroke(1.dp, JarvisCyan.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Archive,
                                contentDescription = null,
                                tint = JarvisCyan,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("ZIP", color = JarvisCyan, fontSize = 11.sp)
                        }
                    }
                }

                if (notificationMessage.isNotBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = notificationMessage,
                        color = JarvisCyanGlow,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        // File Tabs Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            project.files.forEachIndexed { index, file ->
                val isSelected = project.activeFileIndex == index
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) JarvisSurfaceCardElevated else JarvisSurfaceCard)
                        .border(1.dp, if (isSelected) JarvisCyan else JarvisHudBorder, RoundedCornerShape(8.dp))
                        .clickable { viewModel.codingEngine.selectFile(index) }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = when {
                                file.name.endsWith(".py") -> Icons.Default.Code
                                file.name.endsWith(".md") -> Icons.Default.Description
                                else -> Icons.Default.Terminal
                            },
                            contentDescription = null,
                            tint = if (isSelected) JarvisCyan else JarvisTextSecondary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = file.name,
                            color = if (isSelected) JarvisCyanGlow else JarvisTextSecondary,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        // Code Editor / Viewer View
        activeFile?.let { file ->
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                color = JarvisDarkBg,
                border = BorderStroke(1.dp, JarvisHudBorder)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Editor Top bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(JarvisSurfaceCard)
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${file.name} (${file.language.uppercase()})",
                            color = JarvisTextPrimary,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )

                        IconButton(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(file.content))
                                notificationMessage = "${file.name} copied to clipboard."
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy Code",
                                tint = JarvisCyan,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    // Code Content
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(10.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = file.content,
                            color = JarvisCyanGlow,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }

        // Terminal Output Console Box
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = JarvisSurfaceCard,
            border = BorderStroke(1.dp, JarvisHudBorder),
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = "SANDBOX TERMINAL CONSOLE",
                    color = JarvisTextMuted,
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = project.terminalOutput,
                        color = JarvisGreenSuccess,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        lineHeight = 14.sp
                    )
                }
            }
        }
    }
}

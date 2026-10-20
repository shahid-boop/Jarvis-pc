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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.DesktopWindows
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.ToggleOn
import androidx.compose.material.icons.filled.Usb
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Wifi
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AtoZFeature
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

@Composable
fun AtoZPcScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val atozFeatures = viewModel.pcManager.atozCatalog
    var selectedLetter by remember { mutableStateOf<Char?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredList = atozFeatures.filter { feature ->
        val matchesLetter = selectedLetter == null || feature.letter == selectedLetter
        val matchesQuery = searchQuery.isBlank() ||
            feature.title.contains(searchQuery, ignoreCase = true) ||
            feature.titleUrdu.contains(searchQuery, ignoreCase = true) ||
            feature.romanUrdu.contains(searchQuery, ignoreCase = true) ||
            feature.description.contains(searchQuery, ignoreCase = true) ||
            feature.letter.toString().equals(searchQuery.trim(), ignoreCase = true)
        matchesLetter && matchesQuery
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(JarvisDarkBg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
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
                        text = "A TO Z PC SUITE",
                        color = JarvisCyan,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "مکمل پی سی فیچرز",
                        color = JarvisCyanGlow,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "26 Specialized PC automation modules with Pakistani Urdu controls",
                    color = JarvisTextMuted,
                    fontSize = 11.sp
                )
            }
        }

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search A-Z features in English or اردو…", color = JarvisTextMuted, fontSize = 12.sp) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = JarvisCyan,
                    modifier = Modifier.size(18.dp)
                )
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = JarvisSurfaceCard,
                unfocusedContainerColor = JarvisSurfaceCard,
                focusedTextColor = JarvisTextPrimary,
                unfocusedTextColor = JarvisTextPrimary,
                cursorColor = JarvisCyan,
                focusedIndicatorColor = JarvisCyan,
                unfocusedIndicatorColor = JarvisHudBorder
            ),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        // Alphabet Filter Strip (ALL, A, B, C ... Z)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // "ALL" Filter Pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (selectedLetter == null) JarvisCyan else JarvisSurfaceCard)
                    .border(1.dp, if (selectedLetter == null) JarvisCyan else JarvisHudBorder, RoundedCornerShape(6.dp))
                    .clickable { selectedLetter = null }
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "ALL",
                    color = if (selectedLetter == null) Color.Black else JarvisTextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Letters A-Z
            ('A'..'Z').forEach { letter ->
                val isSelected = selectedLetter == letter
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isSelected) JarvisCyan else JarvisSurfaceCard)
                        .border(1.dp, if (isSelected) JarvisCyan else JarvisHudBorder, RoundedCornerShape(6.dp))
                        .clickable { selectedLetter = if (isSelected) null else letter },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = letter.toString(),
                        color = if (isSelected) Color.Black else JarvisTextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(2.dp))

        // Feature Count & Language Guide
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${filteredList.size} MODULES AVAILABLE",
                color = JarvisTextMuted,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )

            Text(
                text = "Tap Execute to run on Windows PC",
                color = JarvisCyanGlow,
                fontSize = 10.sp
            )
        }

        // Feature Cards List
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filteredList, key = { it.code }) { feature ->
                AtoZFeatureCard(
                    feature = feature,
                    onExecute = { viewModel.executeAtoZ(feature) }
                )
            }
        }
    }
}

@Composable
fun AtoZFeatureCard(
    feature: AtoZFeature,
    onExecute: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = JarvisSurfaceCard,
        border = BorderStroke(1.dp, if (feature.isDestructive) JarvisAmberWarning.copy(alpha = 0.5f) else JarvisHudBorder),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("atoz_card_${feature.letter.lowercaseChar()}")
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Big glowing letter badge
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (feature.isDestructive) JarvisAmberWarning.copy(alpha = 0.18f)
                                else JarvisCyan.copy(alpha = 0.15f)
                            )
                            .border(
                                1.dp,
                                if (feature.isDestructive) JarvisAmberWarning else JarvisCyan,
                                RoundedCornerShape(8.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = feature.letter.toString(),
                            color = if (feature.isDestructive) JarvisAmberWarning else JarvisCyan,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = feature.title,
                                color = JarvisTextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "• ${feature.titleUrdu}",
                                color = JarvisCyanGlow,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = "Roman Urdu: “${feature.romanUrdu}”",
                            color = JarvisTextMuted,
                            fontSize = 10.sp
                        )
                    }
                }

                // Category pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(JarvisSurfaceCardElevated)
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = feature.category.uppercase(),
                        color = JarvisTextSecondary,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = feature.description,
                color = JarvisTextSecondary,
                fontSize = 11.sp,
                lineHeight = 15.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Action Execution Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (feature.isDestructive) {
                    Text(
                        text = "Requires Confirmation",
                        color = JarvisAmberWarning,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }

                Button(
                    onClick = onExecute,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (feature.isDestructive) JarvisAmberWarning else JarvisCyan
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Icon(
                        imageVector = when (feature.letter) {
                            'A' -> Icons.Default.Computer
                            'B' -> Icons.Default.BatteryChargingFull
                            'C' -> Icons.Default.ContentCopy
                            'D' -> Icons.Default.DesktopWindows
                            'E' -> Icons.Default.FolderZip
                            'F' -> Icons.Default.Security
                            'G' -> Icons.Default.SportsEsports
                            'H' -> Icons.Default.MonitorHeart
                            'I' -> Icons.Default.Wifi
                            'J' -> Icons.Default.AutoAwesome
                            'K' -> Icons.Default.Keyboard
                            'L' -> Icons.Default.Lock
                            'M' -> Icons.Default.PlayCircle
                            'N' -> Icons.Default.Notifications
                            'O' -> Icons.Default.DocumentScanner
                            'P' -> Icons.Default.Terminal
                            'Q' -> Icons.Default.ToggleOn
                            'R' -> Icons.Default.DeleteSweep
                            'S' -> Icons.Default.CleaningServices
                            'T' -> Icons.Default.Sync
                            'U' -> Icons.Default.Usb
                            'V' -> Icons.Default.VolumeUp
                            'W' -> Icons.Default.Sync
                            'X' -> Icons.Default.Code
                            'Y' -> Icons.Default.Eco
                            'Z' -> Icons.Default.Archive
                            else -> Icons.Default.Computer
                        },
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Execute [${feature.letter}]",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

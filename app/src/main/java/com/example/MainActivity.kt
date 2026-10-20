package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Workspaces
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.ui.ConfirmationRequest
import com.example.ui.JarvisScreen
import com.example.ui.MainViewModel
import com.example.ui.components.ConfirmActionDialog
import com.example.ui.screens.AtoZPcScreen
import com.example.ui.screens.BrainRouterScreen
import com.example.ui.screens.CodingStudioScreen
import com.example.ui.screens.FilesIntelligenceScreen
import com.example.ui.screens.HudScreen
import com.example.ui.screens.MemoryAuditScreen
import com.example.ui.screens.PcControlScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.VoiceChatScreen
import com.example.ui.screens.WorkflowScreen
import com.example.ui.theme.JarvisCyan
import com.example.ui.theme.JarvisCyanGlow
import com.example.ui.theme.JarvisDarkBg
import com.example.ui.theme.JarvisElectricBlue
import com.example.ui.theme.JarvisGreenSuccess
import com.example.ui.theme.JarvisHudBorder
import com.example.ui.theme.JarvisHudBorderActive
import com.example.ui.theme.JarvisSurfaceCard
import com.example.ui.theme.JarvisTextMuted
import com.example.ui.theme.JarvisTextPrimary
import com.example.ui.theme.JarvisTextSecondary
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                JarvisApp(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JarvisApp(viewModel: MainViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val confirmation by viewModel.pendingConfirmation.collectAsState()
    val context = LocalContext.current

    // Request audio record permission on launch
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            // Audio permission handled
        }
    )

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    // BackHandler: return to HUD if on secondary screen
    BackHandler(enabled = currentScreen != JarvisScreen.HUD) {
        viewModel.navigateTo(JarvisScreen.HUD)
    }

    // Confirmation Dialog for High Privilege Actions
    confirmation?.let { request ->
        ConfirmActionDialog(
            title = request.title,
            description = request.description,
            onConfirm = request.onConfirm,
            onDismiss = { viewModel.dismissConfirmation() }
        )
    }

    Scaffold(
        topBar = {
            Surface(
                color = JarvisDarkBg,
                border = androidx.compose.foundation.BorderStroke(1.dp, JarvisHudBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(WindowInsets.statusBars)
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { viewModel.navigateTo(JarvisScreen.HUD) }
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_jarvis_logo),
                            contentDescription = "JARVIS Core Icon",
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "JARVIS",
                                    color = JarvisCyan,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "AI v2.4",
                                    color = JarvisElectricBlue,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            Text(
                                text = currentScreen.title.uppercase(),
                                color = JarvisTextMuted,
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    // Navigation Shortcuts in Top Bar
                    Row(horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(4.dp)) {
                        IconButton(
                            onClick = { viewModel.navigateTo(JarvisScreen.WORKFLOWS) },
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Workspaces,
                                contentDescription = "Workflows",
                                tint = if (currentScreen == JarvisScreen.WORKFLOWS) JarvisCyan else JarvisTextSecondary,
                                modifier = Modifier.size(17.dp)
                            )
                        }
                        IconButton(
                            onClick = { viewModel.navigateTo(JarvisScreen.FILES) },
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Folder,
                                contentDescription = "Files",
                                tint = if (currentScreen == JarvisScreen.FILES) JarvisCyan else JarvisTextSecondary,
                                modifier = Modifier.size(17.dp)
                            )
                        }
                        IconButton(
                            onClick = { viewModel.navigateTo(JarvisScreen.AUDIT) },
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bookmark,
                                contentDescription = "Memory & Logs",
                                tint = if (currentScreen == JarvisScreen.AUDIT) JarvisCyan else JarvisTextSecondary,
                                modifier = Modifier.size(17.dp)
                            )
                        }
                        IconButton(
                            onClick = { viewModel.navigateTo(JarvisScreen.SETTINGS) },
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings",
                                tint = if (currentScreen == JarvisScreen.SETTINGS) JarvisCyan else JarvisTextSecondary,
                                modifier = Modifier.size(17.dp)
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = JarvisDarkBg,
                tonalElevation = 8.dp,
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .border(
                        androidx.compose.foundation.BorderStroke(1.dp, JarvisHudBorder),
                        RoundedCornerShape(0.dp)
                    )
            ) {
                val primaryNavItems = listOf(
                    JarvisScreen.HUD to Icons.Default.Radar,
                    JarvisScreen.ATOZ_PC to Icons.Default.Archive,
                    JarvisScreen.CHAT to Icons.Default.Chat,
                    JarvisScreen.PC_CONTROL to Icons.Default.Computer,
                    JarvisScreen.CODING to Icons.Default.Code,
                    JarvisScreen.BRAIN to Icons.Default.Hub
                )

                primaryNavItems.forEach { (screen, iconVector) ->
                    val isSelected = currentScreen == screen
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.navigateTo(screen) },
                        icon = {
                            Icon(
                                imageVector = iconVector,
                                contentDescription = screen.title,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        label = {
                            Text(
                                text = when (screen) {
                                    JarvisScreen.ATOZ_PC -> "A-Z"
                                    JarvisScreen.PC_CONTROL -> "Control"
                                    JarvisScreen.CODING -> "Code"
                                    JarvisScreen.BRAIN -> "Models"
                                    else -> screen.title.split(" ").first()
                                },
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = JarvisCyan,
                            indicatorColor = JarvisCyan,
                            unselectedIconColor = JarvisTextMuted,
                            unselectedTextColor = JarvisTextMuted
                        ),
                        modifier = Modifier.testTag("nav_${screen.name.lowercase()}")
                    )
                }
            }
        },
        containerColor = JarvisDarkBg
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                JarvisScreen.HUD -> HudScreen(viewModel = viewModel)
                JarvisScreen.ATOZ_PC -> AtoZPcScreen(viewModel = viewModel)
                JarvisScreen.CHAT -> VoiceChatScreen(viewModel = viewModel)
                JarvisScreen.PC_CONTROL -> PcControlScreen(viewModel = viewModel)
                JarvisScreen.WORKFLOWS -> WorkflowScreen(viewModel = viewModel)
                JarvisScreen.CODING -> CodingStudioScreen(viewModel = viewModel)
                JarvisScreen.FILES -> FilesIntelligenceScreen(viewModel = viewModel)
                JarvisScreen.BRAIN -> BrainRouterScreen(viewModel = viewModel)
                JarvisScreen.AUDIT -> MemoryAuditScreen(viewModel = viewModel)
                JarvisScreen.SETTINGS -> SettingsScreen(viewModel = viewModel)
            }
        }
    }
}

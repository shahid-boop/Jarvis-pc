package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.WorkflowPlan
import com.example.data.model.WorkflowStepItem
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
fun WorkflowScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val currentPlan by viewModel.workflowEngine.currentPlan.collectAsState()
    val sampleWorkflows = viewModel.workflowEngine.sampleWorkflows
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(JarvisDarkBg)
            .padding(16.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "AUTONOMOUS WORKFLOW ENGINE",
                    color = JarvisCyan,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "Multi-step plan decomposition • Verification • Error recovery",
                    color = JarvisTextMuted,
                    fontSize = 11.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Preset Workflow Switcher
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            sampleWorkflows.forEach { plan ->
                val selected = currentPlan?.id == plan.id
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (selected) JarvisSurfaceCardElevated else JarvisSurfaceCard,
                    border = BorderStroke(1.dp, if (selected) JarvisCyan else JarvisHudBorder),
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        Column {
                            Text(
                                text = plan.title.take(22) + "…",
                                color = if (selected) JarvisCyanGlow else JarvisTextPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "${plan.steps.size} steps",
                                color = JarvisTextMuted,
                                fontSize = 9.sp
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        currentPlan?.let { plan ->
            // Active Workflow Progress Card
            HudGlassCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = plan.title,
                            color = JarvisTextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = plan.description,
                            color = JarvisTextSecondary,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                if (plan.isRunning) JarvisAmberWarning.copy(alpha = 0.2f)
                                else if (plan.completedCount == plan.steps.size) JarvisGreenSuccess.copy(alpha = 0.2f)
                                else JarvisSurfaceCardElevated
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (plan.isRunning) "RUNNING" else if (plan.completedCount == plan.steps.size) "COMPLETED" else "READY",
                            color = if (plan.isRunning) JarvisAmberWarning else if (plan.completedCount == plan.steps.size) JarvisGreenSuccess else JarvisTextSecondary,
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                val progress = if (plan.steps.isNotEmpty()) plan.completedCount.toFloat() / plan.steps.size else 0f
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = JarvisCyan,
                    trackColor = JarvisHudBorder
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                viewModel.workflowEngine.executeWorkflow(plan.id) { statusMsg ->
                                    viewModel.voiceEngine.speak(statusMsg)
                                }
                            }
                        },
                        enabled = !plan.isRunning,
                        colors = ButtonDefaults.buttonColors(containerColor = JarvisCyan),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Execute Workflow", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }

                    if (plan.isRunning) {
                        OutlinedButton(
                            onClick = { viewModel.workflowEngine.stopWorkflow() },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = JarvisRedAlert),
                            border = BorderStroke(1.dp, JarvisRedAlert),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Stop,
                                contentDescription = null,
                                tint = JarvisRedAlert,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Abort", color = JarvisRedAlert, fontSize = 11.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "PIPELINE EXECUTION STEPS",
                color = JarvisTextMuted,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Step List
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(plan.steps, key = { it.id }) { step ->
                    WorkflowStepCard(step = step)
                }
            }
        }
    }
}

@Composable
fun WorkflowStepCard(step: WorkflowStepItem) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = JarvisSurfaceCard,
        border = BorderStroke(
            1.dp,
            when (step.status) {
                "completed" -> JarvisGreenSuccess.copy(alpha = 0.5f)
                "running" -> JarvisCyanGlow
                "failed" -> JarvisRedAlert
                else -> JarvisHudBorder
            }
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(
                                when (step.status) {
                                    "completed" -> JarvisGreenSuccess.copy(alpha = 0.2f)
                                    "running" -> JarvisCyan.copy(alpha = 0.2f)
                                    "failed" -> JarvisRedAlert.copy(alpha = 0.2f)
                                    else -> JarvisSurfaceCardElevated
                                },
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${step.id}",
                            color = when (step.status) {
                                "completed" -> JarvisGreenSuccess
                                "running" -> JarvisCyan
                                "failed" -> JarvisRedAlert
                                else -> JarvisTextSecondary
                            },
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = step.title,
                            color = JarvisTextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Tool: ${step.toolName}",
                            color = JarvisCyan,
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                Icon(
                    imageVector = when (step.status) {
                        "completed" -> Icons.Default.CheckCircle
                        "running" -> Icons.Default.Sync
                        "failed" -> Icons.Default.Error
                        else -> Icons.Default.HourglassBottom
                    },
                    contentDescription = null,
                    tint = when (step.status) {
                        "completed" -> JarvisGreenSuccess
                        "running" -> JarvisCyanGlow
                        "failed" -> JarvisRedAlert
                        else -> JarvisTextMuted
                    },
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = step.description,
                color = JarvisTextSecondary,
                fontSize = 11.sp,
                lineHeight = 15.sp
            )

            if (step.output.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    color = JarvisDarkBg,
                    shape = RoundedCornerShape(6.dp),
                    border = BorderStroke(1.dp, JarvisHudBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Result: ${step.output}",
                        color = JarvisCyanGlow,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}

package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.data.model.JarvisState
import com.example.ui.theme.JarvisCyan
import com.example.ui.theme.JarvisCyanGlow
import com.example.ui.theme.JarvisElectricBlue
import com.example.ui.theme.JarvisHoloViolet
import com.example.ui.theme.JarvisRedAlert
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun JarvisOrb(
    state: JarvisState,
    amplitudes: List<Float>,
    modifier: Modifier = Modifier,
    size: Dp = 240.dp,
    onClick: () -> Unit = {}
) {
    val infiniteTransition = rememberInfiniteTransition(label = "orb_rotations")

    // Slow continuous outer ring rotation
    val outerAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 14000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "outer_angle"
    )

    // Reverse middle ring rotation
    val innerAngle by infiniteTransition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "inner_angle"
    )

    // Pulse scale for core breathing
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.88f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    // Average audio amplitude
    val avgAmp = if (amplitudes.isNotEmpty()) amplitudes.average().toFloat() else 0.1f
    val dynamicPulse = if (state == JarvisState.SPEAKING || state == JarvisState.LISTENING) {
        pulseScale * (1f + (avgAmp * 0.45f))
    } else {
        pulseScale
    }

    val primaryColor = when (state) {
        JarvisState.IDLE -> JarvisCyan
        JarvisState.LISTENING -> JarvisCyanGlow
        JarvisState.PROCESSING -> JarvisHoloViolet
        JarvisState.SPEAKING -> JarvisElectricBlue
        JarvisState.EXECUTING -> JarvisCyanGlow
    }

    val accentColor = when (state) {
        JarvisState.IDLE -> JarvisElectricBlue
        JarvisState.LISTENING -> JarvisCyan
        JarvisState.PROCESSING -> JarvisHoloViolet
        JarvisState.SPEAKING -> JarvisCyanGlow
        JarvisState.EXECUTING -> JarvisHoloViolet
    }

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(this.size.width / 2f, this.size.height / 2f)
            val radius = (this.size.minDimension / 2f) - 12.dp.toPx()

            // 1. Outer Glow Aura
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        primaryColor.copy(alpha = 0.35f * dynamicPulse),
                        accentColor.copy(alpha = 0.12f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = radius * 1.15f
                ),
                radius = radius * 1.15f,
                center = center
            )

            // 2. Outermost Segmented Arc Ring (Rotates forward)
            val outerDash = PathEffect.dashPathEffect(floatArrayOf(40f, 25f, 15f, 25f), outerAngle)
            drawCircle(
                color = primaryColor.copy(alpha = 0.65f),
                radius = radius,
                center = center,
                style = Stroke(width = 3.dp.toPx(), pathEffect = outerDash)
            )

            // 3. Middle Concentric Counter-Rotating Ring
            val middleDash = PathEffect.dashPathEffect(floatArrayOf(30f, 20f), innerAngle)
            drawCircle(
                color = accentColor.copy(alpha = 0.75f),
                radius = radius * 0.78f,
                center = center,
                style = Stroke(width = 2.dp.toPx(), pathEffect = middleDash)
            )

            // 4. Tick Marks / HUD Tech Nodes
            val tickCount = 12
            for (i in 0 until tickCount) {
                val tickAngle = Math.toRadians((outerAngle + (i * (360f / tickCount))).toDouble())
                val innerR = radius * 0.88f
                val outerR = radius * 0.96f
                val start = Offset(
                    (center.x + innerR * cos(tickAngle)).toFloat(),
                    (center.y + innerR * sin(tickAngle)).toFloat()
                )
                val end = Offset(
                    (center.x + outerR * cos(tickAngle)).toFloat(),
                    (center.y + outerR * sin(tickAngle)).toFloat()
                )
                drawLine(
                    color = primaryColor.copy(alpha = 0.8f),
                    start = start,
                    end = end,
                    strokeWidth = 2.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }

            // 5. Audio Reactive Orbiting Nodes
            amplitudes.take(8).forEachIndexed { index, amp ->
                val nodeAngle = Math.toRadians((innerAngle + (index * 45f)).toDouble())
                val nodeR = radius * (0.6f + (amp * 0.2f))
                val nodePos = Offset(
                    (center.x + nodeR * cos(nodeAngle)).toFloat(),
                    (center.y + nodeR * sin(nodeAngle)).toFloat()
                )
                drawCircle(
                    color = primaryColor.copy(alpha = 0.9f),
                    radius = (2.5f + (amp * 4f)).dp.toPx(),
                    center = nodePos
                )
            }

            // 6. Central Arc Reactor Glowing Core
            val coreRadius = (radius * 0.45f) * dynamicPulse
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White,
                        primaryColor,
                        accentColor.copy(alpha = 0.8f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = coreRadius
                ),
                radius = coreRadius,
                center = center
            )

            // 7. Inner Tech Crosshairs
            val crosshairLen = coreRadius * 0.5f
            drawLine(
                color = Color.White.copy(alpha = 0.7f),
                start = Offset(center.x - crosshairLen, center.y),
                end = Offset(center.x + crosshairLen, center.y),
                strokeWidth = 1.5.dp.toPx()
            )
            drawLine(
                color = Color.White.copy(alpha = 0.7f),
                start = Offset(center.x, center.y - crosshairLen),
                end = Offset(center.x, center.y + crosshairLen),
                strokeWidth = 1.5.dp.toPx()
            )
        }
    }
}

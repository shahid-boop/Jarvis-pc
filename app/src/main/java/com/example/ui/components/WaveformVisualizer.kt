package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.JarvisCyan
import com.example.ui.theme.JarvisElectricBlue
import com.example.ui.theme.JarvisHoloViolet

@Composable
fun WaveformVisualizer(
    amplitudes: List<Float>,
    modifier: Modifier = Modifier,
    height: Dp = 36.dp,
    barCount: Int = 16
) {
    val displayAmps = if (amplitudes.isEmpty()) List(barCount) { 0.1f } else amplitudes.take(barCount)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(height),
        horizontalArrangement = Arrangement.spacedBy(3.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        displayAmps.forEachIndexed { index, amp ->
            val animatedHeightFraction by animateFloatAsState(
                targetValue = amp.coerceIn(0.12f, 1f),
                animationSpec = tween(durationMillis = 80),
                label = "bar_$index"
            )

            Canvas(
                modifier = Modifier
                    .weight(1f)
                    .height(height)
            ) {
                val barWidth = this.size.width
                val barHeight = this.size.height * animatedHeightFraction
                val topY = (this.size.height - barHeight) / 2f

                drawRoundRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            JarvisCyan,
                            JarvisElectricBlue,
                            JarvisHoloViolet
                        ),
                        startY = topY,
                        endY = topY + barHeight
                    ),
                    topLeft = Offset(0f, topY),
                    size = Size(barWidth, barHeight),
                    cornerRadius = CornerRadius(barWidth / 2f, barWidth / 2f)
                )
            }
        }
    }
}

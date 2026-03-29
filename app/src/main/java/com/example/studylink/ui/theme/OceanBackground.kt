package com.example.studylink.ui.theme

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope

@Composable
fun OceanBackground(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "ocean")

    val wave1Offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wave1"
    )

    val wave2Offset by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wave2"
    )

    val wave3Offset by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wave3"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        drawWave(
            color = Color(0xFF4ECDC4).copy(alpha = 0.06f),
            offset = wave1Offset,
            yPosition = size.height * 0.75f,
            amplitude = size.height * 0.04f,
            frequency = 2.5f
        )
        drawWave(
            color = Color(0xFF26C6DA).copy(alpha = 0.05f),
            offset = wave2Offset,
            yPosition = size.height * 0.82f,
            amplitude = size.height * 0.03f,
            frequency = 3f
        )
        drawWave(
            color = Color(0xFF00BCD4).copy(alpha = 0.04f),
            offset = wave3Offset,
            yPosition = size.height * 0.88f,
            amplitude = size.height * 0.025f,
            frequency = 2f
        )
        // Subtle top waves
        drawWave(
            color = Color(0xFF80DEEA).copy(alpha = 0.03f),
            offset = 1f - wave1Offset,
            yPosition = size.height * 0.15f,
            amplitude = size.height * 0.02f,
            frequency = 4f
        )
        drawWave(
            color = Color(0xFF4ECDC4).copy(alpha = 0.04f),
            offset = 1f - wave2Offset,
            yPosition = size.height * 0.08f,
            amplitude = size.height * 0.015f,
            frequency = 3.5f
        )
    }
}

fun DrawScope.drawWave(
    color: Color,
    offset: Float,
    yPosition: Float,
    amplitude: Float,
    frequency: Float
) {
    val path = Path()
    val waveLength = size.width / frequency
    val startX = -waveLength + (offset * waveLength)

    path.moveTo(0f, size.height)
    path.lineTo(startX, yPosition)

    var x = startX
    while (x < size.width + waveLength) {
        val relativeX = x - startX
        val y = yPosition + amplitude * Math.sin(
            (relativeX / waveLength * 2 * Math.PI)
        ).toFloat()
        path.lineTo(x, y)
        x += 2f
    }

    path.lineTo(size.width, size.height)
    path.close()

    drawPath(path = path, color = color)
}
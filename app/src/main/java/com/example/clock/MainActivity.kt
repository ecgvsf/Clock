package com.example.clock

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlin.math.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.tooling.preview.Preview


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ClockApp()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ClockApp() {
    var angle by remember { mutableStateOf(0f) }
    val seconds = ((angle + 90) % 360 / 6).toInt() % 60
    val minutes = seconds / 60

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Selected Time: $minutes minutes $seconds seconds", style = MaterialTheme.typography.h6)
        Spacer(modifier = Modifier.height(20.dp))
        ClockFace(angle = angle, onAngleChange = { newAngle -> angle = newAngle })
    }
}

@Composable
fun ClockFace(angle: Float, onAngleChange: (Float) -> Unit) {
    val radius = 150f
    val markerLength = 20f
    val markers = listOf(0, 15, 30, 45)

    Canvas(modifier = Modifier
        .size((radius * 2).dp)
        .pointerInput(Unit) {
            detectDragGestures { change, _ ->
                val center = Offset(size.width / 2f, size.height / 2f)
                val x = change.position.x - center.x
                val y = change.position.y - center.y
                val newAngle = (atan2(y, x) * 180 / PI).toFloat() + 90
                onAngleChange(newAngle)
            }
        }
        .pointerInput(Unit) {
            detectTapGestures { change ->
                val center = Offset(size.width / 2f, size.height / 2f)
                val x = change.x - center.x
                val y = change.y - center.y
                val tappedAngle = (atan2(y, x) * 180 / PI).toFloat() + 90

                val tappedSecond = ((tappedAngle + 360) % 360 / 6).toInt()
                if (tappedSecond % 15 == 0) {
                    onAngleChange(tappedSecond * 6f - 90)
                }
            }
        }
    ) {
        val center = Offset(size.width / 2f, size.height / 2f)

        // Draw gradient circle
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFFFFFFF),
                    Color(0xFFFFFFFF),
                    Color(0xFFFFFFFF),
                    Color(0xFFEBEBEB)
                ),
                center = center,
                radius = radius
            ),
            radius = radius,
            center = center,
        )

        drawLine(
            color = Color(0xFF0000FF),
            start = center,
            end = Offset(
                center.x + radius * cos((angle - 90) * PI / 180).toFloat(),
                center.y + radius * sin((angle - 90) * PI / 180).toFloat()
            ),
            strokeWidth = 8f,
            cap = StrokeCap.Round
        )
        drawCircle(
            color = Color(0xFF0000FF),
            radius = 8f,
            center = center
        )

        for (marker in markers) {
            val markerAngle = marker * 6 - 90
            val start = Offset(
                center.x + (radius - markerLength) * cos(markerAngle * PI / 180).toFloat(),
                center.y + (radius - markerLength) * sin(markerAngle * PI / 180).toFloat()
            )
            val end = Offset(
                center.x + radius * cos(markerAngle * PI / 180).toFloat(),
                center.y + radius * sin(markerAngle * PI / 180).toFloat()
            )
            drawLine(
                color = Color.Gray,
                start = start,
                end = end,
                strokeWidth = 5f,
                cap = StrokeCap.Round
            )
        }
    }
}
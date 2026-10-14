package com.example.speedometer.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun SpeedScreen(
    speedKmh: Float,
    status: String,
    onStartClick: () -> Unit,
    onStopClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Speed",
            style = MaterialTheme.typography.titleLarge
        )

        Text(
            text = String.format(Locale.getDefault(), "%.2f", speedKmh),
            fontSize = 64.sp
        )

        Text(
            text = "km/h",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = status)

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onStartClick) {
            Text("Start")
        }

        Button(onClick = onStopClick) {
            Text("Stop")
        }
    }
}
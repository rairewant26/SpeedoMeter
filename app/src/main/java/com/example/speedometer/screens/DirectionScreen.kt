package com.example.speedometer.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DirectionScreen(
    azimuth: Float,
    direction: String,
    sensorAvailable: Boolean
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Direction",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            fontSize = 70.sp
        )
        Spacer(modifier = Modifier.height(20.dp))

        if (sensorAvailable) {
            Text(
                text = "${azimuth.toInt()}° $direction",
                fontWeight = FontWeight.SemiBold,
                fontSize = 50.sp
            )
        } else {
            Text("Rotation-vector sensor is unavailable")
        }
    }
}
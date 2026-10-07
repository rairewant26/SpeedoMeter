package com.example.speedometer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.example.speedometer.sensors.DirectionTracker
import com.example.speedometer.ui.theme.SpeedoMeterTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            SpeedoMeterTheme {

                val directionTracker = remember {
                    DirectionTracker(this@MainActivity)
                }

                var azimuth by remember {
                    mutableFloatStateOf(0f)
                }

                var direction by remember {
                    mutableStateOf("N")
                }

                DisposableEffect(Unit) {

                    directionTracker.start()

                    onDispose {
                        directionTracker.stop()
                    }
                }

                LaunchedEffect(Unit) {

                    while (true) {

                        azimuth = directionTracker.azimuth
                        direction = directionTracker.direction

                        delay(100)
                    }
                }

                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    Text(
                        text = "Direction",
                        fontSize = 32.sp
                    )

                    Text(
                        text = "${azimuth.toInt()}°",
                        fontSize = 56.sp
                    )

                    Text(
                        text = direction,
                        fontSize = 40.sp
                    )
                }
            }
        }
    }
}
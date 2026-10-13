package com.example.speedometer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.speedometer.sensors.DirectionTracker
import com.example.speedometer.ui.DirectionScreen
import com.example.speedometer.ui.theme.SpeedoMeterTheme

class MainActivity : ComponentActivity() {

    private lateinit var directionTracker: DirectionTracker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        directionTracker = DirectionTracker(applicationContext)

        setContent {
            SpeedoMeterTheme {
                DirectionScreen(
                    azimuth = directionTracker.azimuth,
                    direction = directionTracker.direction,
                    sensorAvailable = directionTracker.isSensorAvailable
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        directionTracker.start()
    }

    override fun onPause() {
        directionTracker.stop()
        super.onPause()
    }
}
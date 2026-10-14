package com.example.speedometer

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import com.example.speedometer.screens.SpeedScreen
import com.example.speedometer.tracker.SpeedTracker
import com.example.speedometer.ui.theme.SpeedoMeterTheme

class MainActivity : ComponentActivity() {

    private lateinit var speedTracker: SpeedTracker

    private var permissionMessage by mutableStateOf<String?>(null)

    private val locationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->

            val fineGranted =
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true

            val coarseGranted =
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

            if (fineGranted || coarseGranted) {
                permissionMessage = null
                speedTracker.start()
            } else {
                permissionMessage =
                    "Location permission denied. Tap Start to try again."
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        speedTracker = SpeedTracker(applicationContext)

        setContent {
            SpeedoMeterTheme {
                SpeedScreen(
                    speedKmh = speedTracker.speedKmh,
                    status = permissionMessage ?: speedTracker.status,
                    onStartClick = {
                        if (hasLocationPermission()) {
                            permissionMessage = null
                            speedTracker.start()
                        } else {
                            requestLocationPermission()
                        }
                    },
                    onStopClick = {
                        permissionMessage = null
                        speedTracker.stop()
                    }
                )
            }
        }
    }

    override fun onPause() {
        speedTracker.stop()
        super.onPause()
    }

    private fun requestLocationPermission() {
        locationPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        )
    }

    private fun hasLocationPermission(): Boolean {
        val fineGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        return fineGranted || coarseGranted
    }
}
package com.example.speedometer.tracker

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

class SpeedTracker(context: Context) {

    private val fusedLocationClient =
        LocationServices.getFusedLocationProviderClient(
            context.applicationContext
        )

    var speedKmh by mutableFloatStateOf(0f)
        private set

    var status by mutableStateOf("Stopped")
        private set

    private var previousLocation: Location? = null
    private var isRequestingUpdates = false

    private val locationRequest =
        LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            500L
        )
            .setMinUpdateIntervalMillis(250L)
            .build()

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            val location = result.lastLocation ?: return

            // Ignore fixes whose estimated horizontal accuracy is worse than 50 m.
            if (location.hasAccuracy() && location.accuracy > 50f) {
                previousLocation = null
                status = "Waiting for a better GPS fix"
                return
            }

            val speedMetersPerSecond: Float? =
                if (location.hasSpeed()) {
                    location.speed
                } else {
                    calculateSpeedFromLocations(location)
                }

            previousLocation = location

            if (speedMetersPerSecond == null) {
                status = "Waiting for GPS speed"
                return
            }

            val newSpeedKmh = speedMetersPerSecond * 3.6f

            // Show each new reading directly; snap readings below 1 km/h to zero.
            speedKmh = if (newSpeedKmh < 1f) 0f else newSpeedKmh
            status = "Tracking"
        }
    } // Closes locationCallback. Keep this before start().

    @SuppressLint("MissingPermission")
    fun start() {
        if (isRequestingUpdates) return

        status = "Waiting for GPS"
        isRequestingUpdates = true

        try {
            fusedLocationClient
                .requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper()
                )
                .addOnFailureListener { error ->
                    isRequestingUpdates = false
                    status = "Could not start location updates"
                }
        } catch (exception: SecurityException) {
            isRequestingUpdates = false
            status = "Location permission is required"
        }
    }

    @SuppressLint("MissingPermission")
    fun stop() {
        if (isRequestingUpdates) {
            try {
                fusedLocationClient.removeLocationUpdates(locationCallback)
            } catch (exception: SecurityException) {
                // Permission may have been revoked while tracking.
            }
        }

        isRequestingUpdates = false
        previousLocation = null
        speedKmh = 0f
        status = "Stopped"
    }

    private fun calculateSpeedFromLocations(
        currentLocation: Location
    ): Float? {
        val previous = previousLocation ?: return null

        val elapsedSeconds =
            (currentLocation.elapsedRealtimeNanos -
                    previous.elapsedRealtimeNanos) / 1_000_000_000f

        if (elapsedSeconds <= 0f) return null

        return previous.distanceTo(currentLocation) / elapsedSeconds
    }
}
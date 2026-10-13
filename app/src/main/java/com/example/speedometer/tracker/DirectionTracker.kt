package com.example.speedometer.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlin.math.roundToInt

class DirectionTracker(context: Context) : SensorEventListener {

    private val sensorManager =
        context.applicationContext
            .getSystemService(Context.SENSOR_SERVICE) as SensorManager

    private val rotationSensor =
        sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)

    private var isRegistered = false

    var azimuth by mutableFloatStateOf(0f)
        private set

    var direction by mutableStateOf("N")
        private set

    val isSensorAvailable: Boolean
        get() = rotationSensor != null

    private var hasFirstReading = false
    private var smoothedAzimuth = 0f

    fun start(): Boolean {
        if (isRegistered) return true

        val sensor = rotationSensor ?: return false

        isRegistered = sensorManager.registerListener(
            this,
            sensor,
            SensorManager.SENSOR_DELAY_UI
        )

        // The first reading after starting becomes the smoothing baseline.
        hasFirstReading = false

        return isRegistered
    }

    fun stop() {
        if (isRegistered) {
            sensorManager.unregisterListener(this)
            isRegistered = false
        }
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type != Sensor.TYPE_ROTATION_VECTOR) return

        val rotationMatrix = FloatArray(9)

        SensorManager.getRotationMatrixFromVector(
            rotationMatrix,
            event.values
        )

        val orientation = FloatArray(3)

        SensorManager.getOrientation(
            rotationMatrix,
            orientation
        )

        val rawDegrees = Math.toDegrees(
            orientation[0].toDouble()
        ).toFloat()

        val heading = (rawDegrees + 360f) % 360f

        if (!hasFirstReading) {
            smoothedAzimuth = heading
            hasFirstReading = true
        } else {
            // Calculate the shortest change, including when crossing 0°/360°.
            val difference =
                ((heading - smoothedAzimuth + 540f) % 360f) - 180f

            // Smaller multiplier = steadier display, slower response.
            smoothedAzimuth =
                (smoothedAzimuth + difference * 0.12f + 360f) % 360f
        }

        azimuth = (smoothedAzimuth.roundToInt() % 360).toFloat()
        direction = getDirectionName(smoothedAzimuth)
    }

    override fun onAccuracyChanged(
        sensor: Sensor?,
        accuracy: Int
    ) {
        // No action needed for this basic heading display.
    }

    private fun getDirectionName(degrees: Float): String {
        return when {
            degrees >= 337.5f || degrees < 22.5f -> "N"
            degrees < 67.5f -> "NE"
            degrees < 112.5f -> "E"
            degrees < 157.5f -> "SE"
            degrees < 202.5f -> "S"
            degrees < 247.5f -> "SW"
            degrees < 292.5f -> "W"
            else -> "NW"
        }
    }
}
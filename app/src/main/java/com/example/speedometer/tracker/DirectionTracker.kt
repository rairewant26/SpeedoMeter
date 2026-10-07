package com.example.speedometer.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.roundToInt

class DirectionTracker(
    context: Context
) : SensorEventListener {

    private val sensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    private val rotationSensor =
        sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)

    var azimuth: Float = 0f
        private set

    var direction: String = "N"
        private set

    fun start() {

        if (rotationSensor == null) {
            return
        }

        sensorManager.registerListener(
            this,
            rotationSensor,
            SensorManager.SENSOR_DELAY_UI
        )
    }

    fun stop() {

        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {

        if (event == null) return

        if (event.sensor.type != Sensor.TYPE_ROTATION_VECTOR) {
            return
        }

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

        var degrees =
            Math.toDegrees(
                orientation[0].toDouble()
            ).toFloat()

        if (degrees < 0) {
            degrees += 360f
        }

        azimuth = degrees
        direction = getDirectionName(degrees)
    }

    override fun onAccuracyChanged(
        sensor: Sensor?,
        accuracy: Int
    ) {
        // We don't need anything here yet.
    }

    private fun getDirectionName(
        degrees: Float
    ): String {

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
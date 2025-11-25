package com.example.ballmaze

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.unit.dp
import kotlin.math.max
import kotlin.math.min

class MainActivity : ComponentActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var gyroAvailable = true

    private val rollValue = mutableStateOf(0f)
    private val pitchValue = mutableStateOf(0f)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        val gyro = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

        if (gyro == null) gyroAvailable = false
        else sensorManager.registerListener(this, gyro, SensorManager.SENSOR_DELAY_GAME)

        setContent {
            MaterialTheme {
                Surface(Modifier.fillMaxSize()) {
                    BallGame(
                        roll = rollValue.value,
                        pitch = pitchValue.value,
                        gyroAvailable = gyroAvailable
                    )
                }
            }
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_GYROSCOPE) {

            // Gyroscope gives angular velocity → integrate a small amount
            rollValue.value += event.values[1] * 2f     // left/right tilt
            pitchValue.value += event.values[0] * 2f    // forward/back tilt

            // Clamp values
            rollValue.value = rollValue.value.coerceIn(-40f, 40f)
            pitchValue.value = pitchValue.value.coerceIn(-40f, 40f)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}

@Composable
fun BallGame(roll: Float, pitch: Float, gyroAvailable: Boolean) {

    var ballX by remember { mutableStateOf(400f) }
    var ballY by remember { mutableStateOf(600f) }
    val ballRadius = 35f

    val speed = 4f

    // Update ball position based on tilt
    LaunchedEffect(roll, pitch) {
        ballX += -roll * speed
        ballY += pitch * speed
    }

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {

            val width = size.width
            val height = size.height

            /* ---------- Walls / simple rectangular obstacle ---------- */
            val wallLeft = 200f
            val wallTop = 500f
            val wallRight = wallLeft + 300f
            val wallBottom = wallTop + 60f

            // Draw obstacle
            drawRect(
                color = Color.DarkGray,
                topLeft = Offset(wallLeft, wallTop),
                size = androidx.compose.ui.geometry.Size(
                    wallRight - wallLeft,
                    wallBottom - wallTop
                )
            )

            /* ---------- Collision detection ---------- */
            val nextX = ballX.coerceIn(ballRadius, width - ballRadius)
            val nextY = ballY.coerceIn(ballRadius, height - ballRadius)

            // Simple bounding-box collision
            val hitWall =
                nextX + ballRadius > wallLeft &&
                        nextX - ballRadius < wallRight &&
                        nextY + ballRadius > wallTop &&
                        nextY - ballRadius < wallBottom

            if (!hitWall) {
                ballX = nextX
                ballY = nextY
            }

            /* ---------- Draw the ball ---------- */
            drawCircle(
                color = Color.Red,
                radius = ballRadius,
                center = Offset(ballX, ballY)
            )
        }
    }
}

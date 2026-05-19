package com.example.fitnessapp.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.fitnessapp.data.preferences.SettingsDataStore
import com.example.fitnessapp.MainActivity
import com.example.fitnessapp.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate

/**
 * Foreground Service для подсчёта шагов с использованием сенсора шагомера устройства.
 */
class StepCounterService : Service(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var stepSensor: Sensor? = null
    private lateinit var settingsDataStore: SettingsDataStore
    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)
    private var baseStepCount: Double? = null
    private var currentSteps: Int = 0
    private var currentDay: String = LocalDate.now().toString()
    private var isDetectorSensor: Boolean = false

    private val CHANNEL_ID = "step_counter_channel"
    private val NOTIF_ID = 1001

    override fun onCreate() {
        super.onCreate()
        settingsDataStore = SettingsDataStore(applicationContext)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) ?: sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
        isDetectorSensor = stepSensor?.type == Sensor.TYPE_STEP_DETECTOR

        createNotificationChannel()
        startForeground(NOTIF_ID, buildNotification(0))

        serviceScope.launch {
            currentSteps = settingsDataStore.stepsFlow.first()
            stepSensor?.let { sensorManager.registerListener(this@StepCounterService, it, SensorManager.SENSOR_DELAY_NORMAL) }
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Если нужно, можно читать параметры из интента
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
        serviceJob.cancel()
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return

        val value = event.values.getOrNull(0)?.toDouble() ?: return

        serviceScope.launch {
            val today = LocalDate.now().toString()

            if (currentDay != today) {
                currentDay = today
                currentSteps = 0
                settingsDataStore.setSteps(0)
                baseStepCount = null
            }

            val newSteps = if (isDetectorSensor) {
                (currentSteps + 1).coerceAtLeast(0)
            } else {
                val base = baseStepCount ?: (value - currentSteps).also { baseStepCount = it }
                (value - base).toInt().coerceAtLeast(0)
            }

            currentSteps = newSteps
            settingsDataStore.setSteps(currentSteps)

            val notif = buildNotification(currentSteps)
            val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val canPostNotifications = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(this@StepCounterService, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
            } else true

            if (canPostNotifications) {
                nm.notify(NOTIF_ID, notif)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // No-op
    }

    private fun buildNotification(steps: Int): Notification {
        val activityIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, activityIntent, if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT else PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Шаги")
            .setContentText("Шагов: $steps")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, "Step Counter", NotificationManager.IMPORTANCE_LOW)
            val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.createNotificationChannel(channel)
        }
    }
}

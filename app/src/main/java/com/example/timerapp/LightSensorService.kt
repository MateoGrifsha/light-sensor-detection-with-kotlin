package com.example.timerapp

import android.app.*
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat

class LightSensorService : Service(), SensorEventListener {
    private var sensorManager: SensorManager? = null
    private var lightSensor: Sensor? = null
    private val CHANNEL_ID = "LightSensorServiceChannel"

    var hasSentNotif:Boolean = false

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(1, createNotification("Kujdes mos harrosh telefonin."))

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        lightSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_LIGHT)
        sensorManager?.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_LIGHT) {
            val lightLevel = event.values[0]
            if (lightLevel < 35 && !hasSentNotif) {
                sendNotification("Harrove telefonin!" + lightLevel.toString())
                hasSentNotif = true
            }
            else if(lightLevel>60){
                hasSentNotif = false
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onDestroy() {
        super.onDestroy()
        sensorManager?.unregisterListener(this)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun sendNotification(message: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("LightSensorChannel", "Light Sensor Alerts", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, "LightSensorChannel")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Mos Harro Telefonin")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(Notification.DEFAULT_SOUND or Notification.DEFAULT_VIBRATE) // Ensure both sound and vibration
            .setVibrate(longArrayOf(0, 1000, 500, 1000)) // Ensure sound is included
            .setAutoCancel(true)
            .build()

        notificationManager.notify(2, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, "Foreground Light Sensor Service", NotificationManager.IMPORTANCE_LOW)
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(content: String): Notification {
        val notificationChannelId = "light_sensor_channel"

        // Create notification channel (only needed once)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                notificationChannelId,
                "Light Sensor Notifications",
                NotificationManager.IMPORTANCE_LOW  // This ensures sound and other alerts
            ).apply {
                description = "Light sensor alert channel"
                enableLights(true)
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        // Create the notification with the channel ID
        return NotificationCompat.Builder(this, notificationChannelId)
            .setContentTitle("Light Sensor Alert")
            .setContentText(content)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Replace with your app icon
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true) // Prevent swiping away
            .build()
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == "STOP_SERVICE") {
            // Stop the service and finish everything
            stopSelf() // Stop the service
            stopApp() // Stop the app (activity)
        }
        return START_STICKY
    }

    private fun stopApp() {
        // Stop the service and finish the MainActivity
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        android.os.Process.killProcess(android.os.Process.myPid()) // Terminate the process
    }

}

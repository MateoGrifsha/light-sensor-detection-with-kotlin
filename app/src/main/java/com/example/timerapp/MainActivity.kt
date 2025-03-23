package com.example.timerapp

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.content.Intent

class MainActivity : AppCompatActivity() {
    private val CHANNEL_ID = "timer_notification"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // Make sure you have a layout file!

        // Start Light Sensor Foreground Service
        val sensorIntent = Intent(this, LightSensorService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(sensorIntent)
        } else {
            startService(sensorIntent)
        }

        // Create notification channel for alerts
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Timer Notifications"
            val descriptionText = "Channel for timer alerts"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}

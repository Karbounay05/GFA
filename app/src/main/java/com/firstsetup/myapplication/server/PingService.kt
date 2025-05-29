package com.firstsetup.myapplication.server

import android.app.*
import android.content.Intent
import android.os.*
import android.util.Log
import androidx.core.app.NotificationCompat
import okhttp3.*
import java.io.IOException

class PingService : Service() {
    private val handler = Handler(Looper.getMainLooper())
    private val pingInterval = 180_000L // 3 minutes

    private val pingTask = object : Runnable {
        override fun run() {
            pingServer()
            handler.postDelayed(this, pingInterval)
        }
    }

    override fun onCreate() {
        super.onCreate()
        startForeground(1, createNotification())
        handler.post(pingTask)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(pingTask)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun pingServer() {
        val url = "https://fluorescent-boiled-butter.glitch.me/" // your Glitch server

        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("PingService", "Failed to ping server", e)
            }

            override fun onResponse(call: Call, response: Response) {
                Log.d("PingService", "Pinged server: ${response.code}")
            }
        })
    }

    private fun createNotification(): Notification {
        val channelId = "ping_channel"
        val channelName = "Glitch Server Pinger"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Glitch Server Pinger")
            .setContentText("Keeping your Glitch server awake")
            .setSmallIcon(android.R.drawable.ic_popup_sync)
            .build()
    }
}

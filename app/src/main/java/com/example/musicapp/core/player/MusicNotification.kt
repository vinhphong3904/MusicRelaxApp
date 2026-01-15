package com.example.musicapp.core.player

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

object MusicNotification {

    const val CHANNEL_ID = "music_channel"

    fun create(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Music Playback",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }
}

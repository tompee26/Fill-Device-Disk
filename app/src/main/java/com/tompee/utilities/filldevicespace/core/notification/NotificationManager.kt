package com.tompee.utilities.filldevicespace.core.notification

import android.app.NotificationChannel
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.tompee.utilities.filldevicespace.R
import com.tompee.utilities.filldevicespace.di.qualifiers.FromApplication
import com.tompee.utilities.filldevicespace.splash.SplashActivity
import javax.inject.Inject
import javax.inject.Singleton
import android.app.NotificationManager as NotifManager

/**
 * Manages the notification displayed when a fill operation is ongoing
 */
@Singleton
internal class NotificationManager @Inject constructor(
    @FromApplication private val context: Context
) {

    companion object {
        private const val CHANNEL_ID = "FDDChannel"
        private const val CHANNEL_NAME = "Fill Device Disk Channel"
        private const val NOTIFICATION_ID = 1234
    }

    private val notificationBuilder: NotificationCompat.Builder

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotifManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance)
            val notificationManager = context.getSystemService(NotifManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }

        val intent = Intent(context, SplashActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
        notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentTitle(context.getString(R.string.notification_title))
            .setContentText(context.getString(R.string.notification_body))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(false)
            .setOngoing(true)
    }

    fun showNotification() {
        val manager = NotificationManagerCompat.from(context)
        manager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }

    fun cancelNotification() {
        val manager = NotificationManagerCompat.from(context)
        manager.cancel(NOTIFICATION_ID)
    }
}
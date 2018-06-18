package com.tompee.utilities.filldevicespace.core.notification

import android.app.NotificationChannel
import android.content.Context
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import com.tompee.utilities.filldevicespace.R
import com.tompee.utilities.filldevicespace.core.helper.ContentHelper
import android.app.NotificationManager as NotifManager
import android.app.PendingIntent
import android.content.Intent
import com.tompee.utilities.filldevicespace.feature.main.MainActivity


class NotificationManager(private val context: Context,
                          contentHelper: ContentHelper) {

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
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(context, MainActivity::class.java)
        //intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
        notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.launcher_icon)
                .setContentTitle(contentHelper.getString(R.string.notification_title))
                .setContentText(contentHelper.getString(R.string.notification_body))
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
package com.tompee.utilities.filldevicespace.di.module

import android.content.Context
import com.tompee.utilities.filldevicespace.core.helper.ContentHelper
import com.tompee.utilities.filldevicespace.core.notification.NotificationManager
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class NotificationModule {

    @Provides
    @Singleton
    fun provideNotificationManager(context: Context, contentHelper: ContentHelper) = NotificationManager(context, contentHelper)
}
package com.tompee.utilities.filldevicespace.di.component

import android.content.Context
import android.content.SharedPreferences
import com.tompee.utilities.filldevicespace.FillDeviceDiskApp
import com.tompee.utilities.filldevicespace.core.asset.AssetManager
import com.tompee.utilities.filldevicespace.core.helper.ContentHelper
import com.tompee.utilities.filldevicespace.core.helper.FormatHelper
import com.tompee.utilities.filldevicespace.core.notification.NotificationManager
import com.tompee.utilities.filldevicespace.di.module.AppModule
import com.tompee.utilities.filldevicespace.di.module.AssetManagerModule
import com.tompee.utilities.filldevicespace.di.module.NotificationModule
import com.tompee.utilities.filldevicespace.di.module.SharedPreferenceModule
import com.tompee.utilities.filldevicespace.di.module.StorageModule
import com.tompee.utilities.filldevicespace.interactor.FillInteractor
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AppModule::class,
    AssetManagerModule::class,
    SharedPreferenceModule::class,
    StorageModule::class,
    NotificationModule::class])
interface AppComponent {
    fun context(): Context

    fun application(): FillDeviceDiskApp

    fun assetManager(): AssetManager

    fun sharedPreferences(): SharedPreferences

    fun storageInteractor(): FillInteractor

    fun formatHelper(): FormatHelper

    fun contentHelper(): ContentHelper

    fun notificationManager(): NotificationManager
}
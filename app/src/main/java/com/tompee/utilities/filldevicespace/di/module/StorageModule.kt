package com.tompee.utilities.filldevicespace.di.module

import android.content.Context
import android.content.SharedPreferences
import com.tompee.utilities.filldevicespace.core.asset.AssetManager
import com.tompee.utilities.filldevicespace.core.notification.NotificationManager
import com.tompee.utilities.filldevicespace.core.storage.StorageManager
import com.tompee.utilities.filldevicespace.interactor.FillInteractor
import com.tompee.utilities.filldevicespace.interactor.impl.FillInteractorImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class StorageModule {

    @Singleton
    @Provides
    fun provideStorageInteractor(storageInteractorImpl: FillInteractorImpl): FillInteractor = storageInteractorImpl

    @Singleton
    @Provides
    fun provideStorageInteractorImpl(storageManager: StorageManager,
                                     assetManager: AssetManager,
                                     notificationManager: NotificationManager) = FillInteractorImpl(storageManager, assetManager, notificationManager)

    @Singleton
    @Provides
    fun provideStorageManager(context: Context, sharedPreferences: SharedPreferences) = StorageManager(context, sharedPreferences)
}
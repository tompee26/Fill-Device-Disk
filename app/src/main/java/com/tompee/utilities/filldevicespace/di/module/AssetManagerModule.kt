package com.tompee.utilities.filldevicespace.di.module

import android.content.Context
import com.tompee.utilities.filldevicespace.core.asset.AssetManager
import com.tompee.utilities.filldevicespace.core.storage.StorageManager
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AssetManagerModule {
    @Provides
    @Singleton
    fun provideAssetManager(context: Context, storageManager: StorageManager): AssetManager = AssetManager(context, storageManager)
}
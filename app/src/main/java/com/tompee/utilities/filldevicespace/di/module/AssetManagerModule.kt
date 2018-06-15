package com.tompee.utilities.filldevicespace.di.module

import android.content.Context
import com.tompee.utilities.filldevicespace.asset.AssetManager
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AssetManagerModule {
    @Provides
    @Singleton
    fun provideAssetManager(context: Context): AssetManager = AssetManager(context)
}
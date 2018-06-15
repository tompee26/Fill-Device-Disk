package com.tompee.utilities.filldevicespace.di.module

import android.content.Context
import com.tompee.utilities.filldevicespace.FillDeviceDiskApp
import com.tompee.utilities.filldevicespace.core.helper.FormatHelper
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule(private val application: FillDeviceDiskApp) {

    @Provides
    @Singleton
    fun provideApplication(): FillDeviceDiskApp = application

    @Provides
    @Singleton
    fun provideApplicationContext(): Context = application

    @Provides
    @Singleton
    fun provideFormatHelper(): FormatHelper = FormatHelper(application)
}
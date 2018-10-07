package com.tompee.utilities.filldevicespace.di.module

import android.content.Context
import com.tompee.utilities.filldevicespace.Constants
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class SharedPreferenceModule {

    @Provides
    @Singleton
    fun provideSharedPreferences(context: Context) =
            context.getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)!!
}
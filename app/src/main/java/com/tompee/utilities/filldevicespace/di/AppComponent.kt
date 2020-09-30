package com.tompee.utilities.filldevicespace.di

import android.content.Context
import com.tompee.utilities.filldevicespace.FillDeviceDiskApp
import com.tompee.utilities.filldevicespace.di.qualifiers.FromApplication
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidSupportInjectionModule::class,
    ]
)
internal interface AppComponent : AndroidInjector<FillDeviceDiskApp> {

    @Component.Factory
    interface Factory {

        fun create(
            @BindsInstance
            @FromApplication
            applicationContext: Context,
        ): AppComponent
    }
}
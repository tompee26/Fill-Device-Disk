package com.tompee.utilities.filldevicespace

import android.content.Context
import androidx.multidex.MultiDex
import com.tompee.utilities.filldevicespace.di.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import timber.log.Timber

internal class FillDeviceDiskApp : DaggerApplication() {

    companion object {
        private const val ADMOB_APP_ID = "ca-app-pub-1411804566429951~9732472227"
    }

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.factory().create(this)
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}
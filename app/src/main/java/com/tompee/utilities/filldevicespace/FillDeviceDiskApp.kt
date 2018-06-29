package com.tompee.utilities.filldevicespace

import android.app.Application
import com.google.android.gms.ads.MobileAds
import com.tompee.utilities.filldevicespace.di.component.AppComponent
import com.tompee.utilities.filldevicespace.di.component.DaggerAppComponent
import com.tompee.utilities.filldevicespace.di.module.AppModule
import com.tompee.utilities.filldevicespace.di.module.AssetManagerModule

class FillDeviceDiskApp : Application() {
    companion object {
        private const val ADMOB_APP_ID = "ca-app-pub-1411804566429951~9732472227"
    }

    val component: AppComponent by lazy {
        DaggerAppComponent.builder()
                .appModule(AppModule(this))
                .assetManagerModule(AssetManagerModule())
                .build()
    }

    override fun onCreate() {
        super.onCreate()
        MobileAds.initialize(applicationContext, ADMOB_APP_ID)
    }
}
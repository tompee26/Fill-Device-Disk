package com.tompee.utilities.filldevicespace.di.module

import com.tompee.utilities.filldevicespace.about.AboutActivity
import com.tompee.utilities.filldevicespace.fill.FillActivity
import com.tompee.utilities.filldevicespace.fill.FillModule
import com.tompee.utilities.filldevicespace.splash.SplashActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
internal abstract class ActivityBindingModule {

    @ContributesAndroidInjector
    abstract fun bindSplashActivity(): SplashActivity

    @ContributesAndroidInjector(modules = [FillModule::class])
    abstract fun bindFillActivity(): FillActivity

    @ContributesAndroidInjector
    abstract fun bindAboutActivity(): AboutActivity
}
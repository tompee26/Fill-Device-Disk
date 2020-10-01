package com.tompee.utilities.filldevicespace.di.module

import com.tompee.utilities.filldevicespace.about.AboutActivity
import com.tompee.utilities.filldevicespace.di.scopes.ActivityScope
import com.tompee.utilities.filldevicespace.fill.FillActivity
import com.tompee.utilities.filldevicespace.fill.FillModule
import com.tompee.utilities.filldevicespace.legal.LicenseActivity
import com.tompee.utilities.filldevicespace.legal.PrivacyPolicyActivity
import com.tompee.utilities.filldevicespace.splash.SplashActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
internal abstract class ActivityBindingModule {

    @ContributesAndroidInjector
    abstract fun bindSplashActivity(): SplashActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [FillModule::class])
    abstract fun bindFillActivity(): FillActivity

    @ContributesAndroidInjector
    abstract fun bindAboutActivity(): AboutActivity

    @ContributesAndroidInjector
    abstract fun bindPrivacyPolicyActivity(): PrivacyPolicyActivity

    @ContributesAndroidInjector
    abstract fun bindLicenseActivity(): LicenseActivity
}
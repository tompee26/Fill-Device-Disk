package com.tompee.utilities.filldevicespace.di.component

import com.tompee.utilities.filldevicespace.di.module.MainModule
import com.tompee.utilities.filldevicespace.di.scopes.MainScope
import com.tompee.utilities.filldevicespace.feature.main.NewMainActivity
import com.tompee.utilities.filldevicespace.feature.main.advancefill.AdvanceFillFragment
import com.tompee.utilities.filldevicespace.feature.main.easyfill.EasyFillFragment
import com.tompee.utilities.filldevicespace.feature.main.storage.CheckStorageFragment

import dagger.Component

@MainScope
@Component(dependencies = [AppComponent::class],
        modules = [MainModule::class])
interface MainComponent {
    fun inject(activity: NewMainActivity)

    fun inject(activity: EasyFillFragment)

    fun inject(advanceFillFragment: AdvanceFillFragment)

    fun easyFillFragment(): EasyFillFragment

    fun advanceFillFragment(): AdvanceFillFragment

    fun checkStorageFragment(): CheckStorageFragment
}
package com.tompee.utilities.filldevicespace.di.module

import android.content.SharedPreferences
import com.tompee.utilities.filldevicespace.core.helper.FormatHelper
import com.tompee.utilities.filldevicespace.di.scopes.MainScope
import com.tompee.utilities.filldevicespace.feature.main.MainPresenter
import com.tompee.utilities.filldevicespace.feature.main.advancefill.AdvanceFillFragment
import com.tompee.utilities.filldevicespace.feature.main.advancefill.AdvanceFillPresenter
import com.tompee.utilities.filldevicespace.feature.main.easyfill.EasyFillFragment
import com.tompee.utilities.filldevicespace.feature.main.easyfill.EasyFillPresenter
import com.tompee.utilities.filldevicespace.feature.main.storage.CheckStorageFragment
import com.tompee.utilities.filldevicespace.feature.main.storage.CheckStoragePresenter
import com.tompee.utilities.filldevicespace.interactor.FillInteractor
import dagger.Module
import dagger.Provides

@Module
class MainModule {
    @MainScope
    @Provides
    fun provideMainPresenter(sharedPreferences: SharedPreferences): MainPresenter = MainPresenter(sharedPreferences)

    @MainScope
    @Provides
    fun provideEasyFillFragment(): EasyFillFragment = EasyFillFragment()

    @MainScope
    @Provides
    fun provideEasyFillPresenter(fillInteractor: FillInteractor, formatHelper: FormatHelper): EasyFillPresenter = EasyFillPresenter(fillInteractor, formatHelper)

    @MainScope
    @Provides
    fun provideAdvanceFillFragment(): AdvanceFillFragment = AdvanceFillFragment()

    @MainScope
    @Provides
    fun provideAdvanceFillPresenter(fillInteractor: FillInteractor, formatHelper: FormatHelper): AdvanceFillPresenter = AdvanceFillPresenter(fillInteractor, formatHelper)

    @MainScope
    @Provides
    fun provideCheckStorageFragment(): CheckStorageFragment = CheckStorageFragment()

    @MainScope
    @Provides
    fun provideCheckStoragePresenter(fillInteractor: FillInteractor, formatHelper: FormatHelper): CheckStoragePresenter = CheckStoragePresenter(fillInteractor, formatHelper)
}
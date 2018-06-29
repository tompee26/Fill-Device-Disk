package com.tompee.utilities.filldevicespace.di.module

import android.content.SharedPreferences
import com.tompee.utilities.filldevicespace.core.helper.ContentHelper
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
    fun provideMainPresenter(sharedPreferences: SharedPreferences) = MainPresenter(sharedPreferences)

    @MainScope
    @Provides
    fun provideEasyFillFragment() = EasyFillFragment()

    @MainScope
    @Provides
    fun provideEasyFillPresenter(fillInteractor: FillInteractor,
                                 formatHelper: FormatHelper,
                                 contentHelper: ContentHelper) = EasyFillPresenter(fillInteractor, formatHelper, contentHelper)

    @MainScope
    @Provides
    fun provideAdvanceFillFragment() = AdvanceFillFragment()

    @MainScope
    @Provides
    fun provideAdvanceFillPresenter(fillInteractor: FillInteractor,
                                    formatHelper: FormatHelper,
                                    contentHelper: ContentHelper) = AdvanceFillPresenter(fillInteractor, formatHelper, contentHelper)

    @MainScope
    @Provides
    fun provideCheckStorageFragment() = CheckStorageFragment()

    @MainScope
    @Provides
    fun provideCheckStoragePresenter(fillInteractor: FillInteractor,
                                     formatHelper: FormatHelper,
                                     contentHelper: ContentHelper) = CheckStoragePresenter(fillInteractor, formatHelper, contentHelper)
}
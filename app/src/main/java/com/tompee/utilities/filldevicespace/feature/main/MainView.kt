package com.tompee.utilities.filldevicespace.feature.main

import com.tompee.utilities.filldevicespace.base.BaseMvpView
import io.reactivex.Completable

interface MainView : BaseMvpView {
    fun setupView(adapter: MainViewPagerAdapter)
    fun showAppRater()
}
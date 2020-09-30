package com.tompee.utilities.filldevicespace.feature.main

import com.tompee.utilities.filldevicespace.common.BaseMvpView

interface MainView : BaseMvpView {
    fun setupView(adapter: MainViewPagerAdapter)
    fun showAppRater()
}
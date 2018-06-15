package com.tompee.utilities.filldevicespace.feature.main

import android.content.SharedPreferences
import com.tompee.utilities.filldevicespace.Constants
import com.tompee.utilities.filldevicespace.base.BasePresenter

class MainPresenter(private val sharedPreferences: SharedPreferences) : BasePresenter<MainView>() {
    override fun onAttachView() {
        showAppRater()
    }

    override fun onDetachView() {
    }

    private fun showAppRater() {
        val editor = sharedPreferences.edit()
        val launchCount = sharedPreferences.getInt(Constants.SP_TAG_LAUNCH_COUNT, 0)
        if (launchCount == Constants.MIN_LAUNCH_COUNT) {
            editor.putInt(Constants.SP_TAG_LAUNCH_COUNT, 0)
            view.showAppRater()
        } else {
            editor.putInt(Constants.SP_TAG_LAUNCH_COUNT, launchCount + 1)
        }
        editor.apply()
    }
}
package com.tompee.utilities.filldevicespace.feature.main

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.tompee.utilities.filldevicespace.R
import com.tompee.utilities.filldevicespace.di.component.MainComponent

class MainViewPagerAdapter(private val context: Context,
                           fragmentManager: FragmentManager,
                           private val component: MainComponent) : FragmentPagerAdapter(fragmentManager) {
    companion object {
        private const val PAGE_COUNT = 3
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> component.easyFillFragment()
            1 -> component.advanceFillFragment()
            else -> component.checkStorageFragment()
        }
    }

    override fun getCount(): Int = PAGE_COUNT

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> context.getString(R.string.ids_lbl_easy_fill)
            1 -> context.getString(R.string.ids_lbl_advance_fill)
            else -> context.getString(R.string.ids_lbl_check_storage)
        }
    }
}
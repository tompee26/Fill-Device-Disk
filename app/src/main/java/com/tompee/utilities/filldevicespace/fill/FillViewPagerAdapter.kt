package com.tompee.utilities.filldevicespace.fill

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.tompee.utilities.filldevicespace.common.factory.FragmentFactory
import com.tompee.utilities.filldevicespace.fill.storage.StorageFragment
import javax.inject.Inject

/**
 * View pager adapter
 * Handles switch between different fill fragments
 */
internal class FillViewPagerAdapter @Inject constructor(
    private val fragmentActivity: FragmentActivity,
    private val fragmentFactory: FragmentFactory
) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = 1

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> fragmentFactory.instantiate(
                fragmentActivity.classLoader,
                StorageFragment::class.java.name
            )
            else -> throw IllegalStateException("Position not found")
        }
    }
}
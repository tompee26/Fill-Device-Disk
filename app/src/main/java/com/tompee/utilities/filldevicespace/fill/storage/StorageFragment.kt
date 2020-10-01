package com.tompee.utilities.filldevicespace.fill.storage

import android.os.Bundle
import android.view.View
import com.tompee.utilities.filldevicespace.R
import com.tompee.utilities.filldevicespace.common.factory.ViewModelFactory
import com.tompee.utilities.filldevicespace.common.ui.BaseFragment
import com.tompee.utilities.filldevicespace.databinding.FragmentStorageBinding
import javax.inject.Inject

/**
 * Storage fragment
 */
internal class StorageFragment @Inject constructor(
    private val viewModelFactory: ViewModelFactory
) : BaseFragment<FragmentStorageBinding>() {

    override val layoutId: Int = R.layout.fragment_storage

    private val viewModel by lazy { viewModelFactory.get<StorageViewModel>(Scope.THIS) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.viewModel = viewModel
        with(viewBinding.chart) {
            setUsePercentValues(true)
            setDescription("")
            setExtraOffsets(5f, 10f, 5f, 5f)
            dragDecelerationFrictionCoef = 0.95f
            setTouchEnabled(false)
            legend.isEnabled = false

            isDrawHoleEnabled = true
            setHoleColor(android.R.color.transparent)
            setDrawCenterText(false)

            holeRadius = 0f
            transparentCircleRadius = 0f
            setDrawCenterText(true)
            rotationAngle = 0f
            setDrawEntryLabels(false)
            setEntryLabelTextSize(12f)
        }
    }
}
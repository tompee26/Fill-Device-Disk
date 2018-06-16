package com.tompee.utilities.filldevicespace.feature.main.storage

import android.os.Bundle
import android.view.View
import com.github.mikephil.charting.data.PieData
import com.tompee.utilities.filldevicespace.FillDeviceDiskApp
import com.tompee.utilities.filldevicespace.R
import com.tompee.utilities.filldevicespace.base.BaseFragment
import com.tompee.utilities.filldevicespace.di.component.DaggerMainComponent
import kotlinx.android.synthetic.main.fragment_check_storage.*
import javax.inject.Inject

class CheckStorageFragment : BaseFragment(), CheckStorageView {
    @Inject
    lateinit var presenter: CheckStoragePresenter

    //region Lifecycle
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        chart.setUsePercentValues(true)
        chart.setDescription("")
        chart.setExtraOffsets(5f, 10f, 5f, 5f)
        chart.dragDecelerationFrictionCoef = 0.95f
        chart.setTouchEnabled(false)
        chart.legend.isEnabled = false

        chart.isDrawHoleEnabled = true
        chart.setHoleColor(android.R.color.transparent)
        chart.setDrawCenterText(false)

        chart.holeRadius = 0f
        chart.transparentCircleRadius = 0f
        chart.setDrawCenterText(true)
        chart.rotationAngle = 0f
        chart.setDrawEntryLabels(false)
        chart.setEntryLabelTextSize(12f)
        presenter.attachView(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

    //endregion

    //region BaseFragment
    override fun setupComponent() {
        val component = DaggerMainComponent.builder()
                .appComponent((activity?.application as FillDeviceDiskApp).component)
                .build()
        component.inject(this)
    }

    override fun layoutId(): Int = R.layout.fragment_check_storage
    //endregion

    //region View
    override fun setFreeSpace(space: String) {
        freeSpace.text = space
    }

    override fun setFillSpace(space: String) {
        fillSpace.text = space
    }

    override fun setMaxSpace(space: String) {
        system.text = space
    }

    override fun setData(data: PieData) {
        chart.data = data
        chart.highlightValues(null)
        chart.invalidate()
    }
    //region
}
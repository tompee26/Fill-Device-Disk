package com.tompee.utilities.filldevicespace.feature.main.storage

import com.github.mikephil.charting.data.PieData
import com.tompee.utilities.filldevicespace.base.BaseMvpView

interface CheckStorageView : BaseMvpView {
    fun setData(data: PieData)

    fun setFreeSpace(space: String)

    fun setFillSpace(space: String)

    fun setMaxSpace(space: String)
}
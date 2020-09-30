package com.tompee.utilities.filldevicespace.feature.main.storage

import com.github.mikephil.charting.data.PieData
import com.tompee.utilities.filldevicespace.common.BaseMvpView
import io.reactivex.Observable

interface CheckStorageView : BaseMvpView {
    fun refreshObservable() : Observable<Any>

    fun sdCardObservable(): Observable<Any>

    fun setData(data: PieData)

    fun setFreeSpace(space: String)

    fun setFillSpace(space: String)

    fun setMaxSpace(space: String)

    fun setSdCardButtonState(state: Boolean)

    fun setSdCardButtonBackground(color : Int)
}
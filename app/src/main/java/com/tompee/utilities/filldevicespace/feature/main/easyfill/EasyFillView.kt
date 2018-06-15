package com.tompee.utilities.filldevicespace.feature.main.easyfill

import com.tompee.utilities.filldevicespace.base.BaseMvpView
import io.reactivex.Observable

interface EasyFillView : BaseMvpView {
    fun startObservable(): Observable<Any>

    fun clearObservable(): Observable<Any>

    fun setFreeSpace(space: String)

    fun setFillSpace(space: String)

    fun setPercentage(percentage: Double)

    fun setSpeed(text: String)

    fun setFillState(state: Boolean)
}
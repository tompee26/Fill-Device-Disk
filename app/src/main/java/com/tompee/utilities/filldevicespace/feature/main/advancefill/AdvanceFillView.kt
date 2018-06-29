package com.tompee.utilities.filldevicespace.feature.main.advancefill

import com.tompee.utilities.filldevicespace.feature.main.easyfill.EasyFillView
import io.reactivex.Observable

interface AdvanceFillView : EasyFillView {
    fun getGbObservable(): Observable<Int>

    fun getMbObservable(): Observable<Int>

    fun setStartButtonState(state : Boolean)
}
package com.tompee.utilities.filldevicespace.interactor

import io.reactivex.Observable

interface FillInteractor {
    fun getFreeSpaceObservable(): Observable<Long>

    fun getFillSpaceObservable(): Observable<Long>

    fun getPercentageObservable(): Observable<Double>

    fun getSpeedObservable(): Observable<Double>

    fun getMaxStorageSpaceObservable(): Observable<Long>

    fun startFill(limit: Long = 0L): Observable<Int>

    fun clearFill()

    fun refresh()

    fun isRemovableStorageSupported(): Boolean
}
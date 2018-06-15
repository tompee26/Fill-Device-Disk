package com.tompee.utilities.filldevicespace.interactor

import io.reactivex.Observable

interface FillInteractor {
    fun getFreeSpaceObservable(): Observable<Long>

    fun getFillSpaceObservable(): Observable<Long>

    fun getPercentageObservable(): Observable<Double>

    fun getSpeedObservable(): Observable<Double>

    fun getMaxStorageSpace(): Long

    fun startFill(): Observable<Long>

    fun clearFill()
}
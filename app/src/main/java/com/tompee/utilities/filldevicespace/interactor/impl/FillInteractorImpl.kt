package com.tompee.utilities.filldevicespace.interactor.impl

import com.tompee.utilities.filldevicespace.Constants
import com.tompee.utilities.filldevicespace.asset.AssetManager
import com.tompee.utilities.filldevicespace.core.storage.StorageManager
import com.tompee.utilities.filldevicespace.interactor.FillInteractor
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import java.io.IOException
import java.util.concurrent.TimeUnit

class FillInteractorImpl(private val storageManager: StorageManager,
                         private val assetManager: AssetManager) : FillInteractor {
    private val freeSpaceSubject = BehaviorSubject.create<Long>()
    private val fillSpaceSubject = BehaviorSubject.create<Long>()
    private val percentageSubject = BehaviorSubject.create<Double>()
    private val totalStorageSize: Long = storageManager.getTotalStorageSize() /* cache total size */
    private val speedSubject = BehaviorSubject.create<Double>()

    init {
        updateAll(0.0)
    }

    override fun getFreeSpaceObservable(): Observable<Long> = freeSpaceSubject

    override fun getFillSpaceObservable(): Observable<Long> = fillSpaceSubject

    override fun getPercentageObservable(): Observable<Double> = percentageSubject

    override fun getSpeedObservable(): Observable<Double> = speedSubject

    override fun startFill(): Observable<Long> {
        return Observable.interval(10, TimeUnit.MILLISECONDS)
                .map {
                    try {
                        val start = System.nanoTime()
                        val currentAsset = assetManager.getAsset(storageManager.getAvailableStorageSize())
                        assetManager.copyAssetsFile(currentAsset!!, currentAsset + it)
                        val timeElapsed = System.nanoTime() - start
                        updateAll(computeSpeed(currentAsset, timeElapsed))
                    } catch (e: IOException) {
                        updateAll(0.0)
                        throw e
                    }
                    return@map it
                }
                .onErrorReturn {
                    return@onErrorReturn 0
                }
    }

    override fun clearFill() {
        storageManager.deleteFiles()
        updateAll(0.0)
    }

    override fun getMaxStorageSpace(): Long = totalStorageSize

    private fun computeSpeed(asset: String, timeElapsed: Long): Double {
        return assetManager.getAssetSize(asset)!!.toDouble() / timeElapsed * Constants.SPEED_FACTOR
    }

    private fun updateAll(speed: Double) {
        val freeSpace = storageManager.getAvailableStorageSize()
        freeSpaceSubject.onNext(freeSpace)
        percentageSubject.onNext(((totalStorageSize - freeSpace).toDouble() / totalStorageSize))
        fillSpaceSubject.onNext(storageManager.getFillSize())
        speedSubject.onNext(speed)
    }
}
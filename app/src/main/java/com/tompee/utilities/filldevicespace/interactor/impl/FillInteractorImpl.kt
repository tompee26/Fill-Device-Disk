package com.tompee.utilities.filldevicespace.interactor.impl

import com.tompee.utilities.filldevicespace.Constants
import com.tompee.utilities.filldevicespace.core.asset.AssetManager
import com.tompee.utilities.filldevicespace.core.notification.NotificationManager
import com.tompee.utilities.filldevicespace.core.storage.StorageManager
import com.tompee.utilities.filldevicespace.interactor.FillInteractor
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import java.io.IOException
import java.util.concurrent.TimeUnit

class FillInteractorImpl(private val storageManager: StorageManager,
                         private val assetManager: AssetManager,
                         private val notificationManager: NotificationManager) : FillInteractor {
    private val freeSpaceSubject = BehaviorSubject.create<Long>()
    private val fillSpaceSubject = BehaviorSubject.create<Long>()
    private val percentageSubject = BehaviorSubject.create<Double>()
    private val totalStorageSubject = BehaviorSubject.create<Long>()
    private val totalStorageSize: Long = storageManager.getTotalStorageSize() /* cache total size */
    private val speedSubject = BehaviorSubject.create<Double>()

    init {
        totalStorageSubject.onNext(totalStorageSize)
        updateAll(0.0)
    }

    override fun getFreeSpaceObservable(): Observable<Long> = freeSpaceSubject

    override fun getFillSpaceObservable(): Observable<Long> = fillSpaceSubject

    override fun getPercentageObservable(): Observable<Double> = percentageSubject

    override fun getSpeedObservable(): Observable<Double> = speedSubject

    override fun startFill(limit: Long): Observable<Int> {
        return Observable.interval(10, TimeUnit.MILLISECONDS)
                .map {
                    storageManager.getFileCount()
                }
                .map {
                    if (limit == 0L) {
                        return@map freeFill(it)
                    }
                    return@map customFill(it, limit)
                }
                .onErrorReturn {
                    return@onErrorReturn 0
                }
                .doOnSubscribe { notificationManager.showNotification() }
                .doFinally { notificationManager.cancelNotification() }
    }

    override fun clearFill() {
        storageManager.deleteFiles()
        updateAll(0.0)
    }

    override fun getMaxStorageSpaceObservable(): Observable<Long> = totalStorageSubject

    override fun refresh() {
        updateAll(0.0)
    }

    override fun isRemovableStorageSupported(): Boolean = storageManager.getRemovableStorage() != null

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

    private fun freeFill(index: Int): Int {
        try {
            val start = System.nanoTime()
            val currentAsset = assetManager.getAsset(storageManager.getAvailableStorageSize())
            assetManager.copyAssetsFile(currentAsset!!, currentAsset + index)
            val timeElapsed = System.nanoTime() - start
            updateAll(computeSpeed(currentAsset, timeElapsed))
        } catch (e: IOException) {
            updateAll(0.0)
            throw e
        }
        return index
    }

    private fun customFill(index: Int, limit: Long): Int {
        try {
            val fillSize = storageManager.getFillSize()
            if (fillSize >= limit) {
                throw IOException("Limit reached")
            }
            val start = System.nanoTime()
            val currentAsset = assetManager.getAsset(limit - fillSize)
            assetManager.copyAssetsFile(currentAsset!!, currentAsset + index)
            val timeElapsed = System.nanoTime() - start
            updateAll(computeSpeed(currentAsset, timeElapsed))
        } catch (e: IOException) {
            updateAll(0.0)
            throw e
        }
        return index
    }
}
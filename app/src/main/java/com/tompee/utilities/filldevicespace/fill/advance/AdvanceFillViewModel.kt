package com.tompee.utilities.filldevicespace.fill.advance

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.jakewharton.rx.replayingShare
import com.tompee.utilities.filldevicespace.common.ui.BaseViewModel
import com.tompee.utilities.filldevicespace.core.helpers.FormatHelper
import com.tompee.utilities.filldevicespace.core.storage.DiskStorageManager
import com.tompee.utilities.filldevicespace.di.qualifiers.FromApplication
import io.reactivex.disposables.Disposables
import io.reactivex.disposables.SerialDisposable
import io.reactivex.rxkotlin.Observables
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

/**
 * Easy fill view model
 * Communicates with the core to update storage information
 */
internal class AdvanceFillViewModel @Inject constructor(
    @FromApplication private val context: Context,
    private val storageManager: DiskStorageManager,
    private val formatHelper: FormatHelper
) : BaseViewModel() {

    private val sharedFillSize = storageManager.watchFillSize()
        .replayingShare()

    private val sharedFreeSize = storageManager.getAvailableStorageSize()
        .replayingShare()

    private val fillSubscription = SerialDisposable()

    /**
     * Emits the current fill size
     */
    val fillSize = sharedFillSize
        .map(formatHelper::formatFileSize)
        .toLiveData()

    /**
     * Emits the current available storage size
     */
    val freeSize = sharedFreeSize
        .map(formatHelper::formatFileSize)
        .toLiveData()

    /**
     * Occupied space percentage
     */
    val percentage = Observables.combineLatest(
        storageManager.getTotalStorageSize().toObservable(),
        sharedFreeSize
    ) { total, free -> (total - free).toFloat() * 100 / total }
        .toLiveData()

    /**
     * Emits the current fill speed
     */
    val speed = MutableLiveData(formatHelper.formatSpeed(0.0))

    /**
     * Indicates if fill operation is ongoing
     */
    val isFilling = MutableLiveData(false)

    /**
     * Starts the filling operation
     */
    fun startFill() {
        fillSubscription.set(storageManager.startFill()
            .doOnSubscribe { isFilling.postValue(true) }
            .doFinally {
                isFilling.postValue(false)
                speed.postValue(formatHelper.formatSpeed(0.0))
            }
            .subscribeOn(Schedulers.computation())
            .subscribe({ speed.postValue(formatHelper.formatSpeed(it)) }, Timber::e)
        )
    }

    /**
     * Stops the ongoing fill operation
     */
    fun stopFill() = fillSubscription.set(Disposables.empty())

    /**
     * Deletes all the fillers
     */
    fun clearFill() = storageManager.clearFill()

    /**
     * Invalidates the information
     */
    fun invalidate() = storageManager.invalidate()

    override fun onCleared() {
        super.onCleared()
        fillSubscription.set(Disposables.empty())
    }
}
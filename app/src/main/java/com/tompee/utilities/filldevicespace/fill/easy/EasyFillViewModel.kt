package com.tompee.utilities.filldevicespace.fill.easy

import android.content.Context
import com.jakewharton.rx.replayingShare
import com.tompee.utilities.filldevicespace.common.ui.BaseViewModel
import com.tompee.utilities.filldevicespace.core.helpers.FormatHelper
import com.tompee.utilities.filldevicespace.core.storage.DiskStorageManager
import com.tompee.utilities.filldevicespace.di.qualifiers.FromApplication
import io.reactivex.rxkotlin.Observables
import javax.inject.Inject

/**
 * Easy fill view model
 * Communicates with the core to update storage information
 */
internal class EasyFillViewModel @Inject constructor(
    @FromApplication private val context: Context,
    private val storageManager: DiskStorageManager,
    private val formatHelper: FormatHelper
) : BaseViewModel() {

    private val sharedFillSize = storageManager.watchFillSize()
        .replayingShare()

    private val sharedFreeSize = storageManager.getAvailableStorageSize()
        .replayingShare()

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
     * Emits the storage space occupied by the system
     */
    val systemSize = Observables.combineLatest(
        storageManager.getTotalStorageSize().toObservable(),
        sharedFillSize, sharedFreeSize
    ) { total, fill, free -> total - fill - free }
        .map(formatHelper::formatFileSize)
        .toLiveData()

    /**
     * Invalidates the information
     */
    fun invalidate() = storageManager.invalidate()
}
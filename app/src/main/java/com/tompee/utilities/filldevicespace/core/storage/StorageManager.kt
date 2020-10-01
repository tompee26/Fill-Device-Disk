package com.tompee.utilities.filldevicespace.core.storage

import io.reactivex.Observable
import io.reactivex.Single

/**
 * Defines the storage management functions
 */
internal interface StorageManager {

    /**
     * Returns the available storage size
     *
     * @return the available storage size
     */
    fun getAvailableStorageSize(): Observable<Long>

    /**
     * Returns the total fill size
     *
     * @return the total fill size
     */
    fun watchFillSize(): Observable<Long>

    /**
     * Returns the total storage size
     *
     * @return the total storage size
     */
    fun getTotalStorageSize(): Single<Long>

    /**
     * Returns the inherent directory
     *
     * @return the inherent directory
     */
    fun getDirectory(): String
}
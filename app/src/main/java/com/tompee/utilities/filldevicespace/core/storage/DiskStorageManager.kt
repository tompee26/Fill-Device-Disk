package com.tompee.utilities.filldevicespace.core.storage

import android.content.Context
import android.os.Build
import android.os.StatFs
import com.tompee.utilities.filldevicespace.Constants
import com.tompee.utilities.filldevicespace.core.asset.AssetManager
import com.tompee.utilities.filldevicespace.core.notification.NotificationManager
import com.tompee.utilities.filldevicespace.di.qualifiers.FromApplication
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject
import java.io.File
import java.io.FileOutputStream
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages disk state and information.
 *
 * Always use this class to access storage as this class does some precomputations to speed up
 * size computations
 */
@Singleton
internal class DiskStorageManager @Inject constructor(
    @FromApplication private val context: Context,
    private val assetManager: AssetManager,
    private val notificationManager: NotificationManager
) : StorageManager {

    /**
     * Precomputed fill size
     */
    private val fillSize = BehaviorSubject.createDefault(computeFillSize()).toSerialized()

    /**
     * Returns the available storage size
     *
     * @return the available storage size
     */
    override fun getAvailableStorageSize(): Observable<Long> {
        return fillSize.map { internalGetAvailableStorageSize() }
    }

    /**
     * Computes the available storage size
     */
    private fun internalGetAvailableStorageSize(): Long {
        val statFs = StatFs(getDirectory())
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) { //API 18
            val blockSize = statFs.blockSize.toLong()
            statFs.availableBlocks * blockSize
        } else {
            statFs.availableBytes
        }
    }

    /**
     * Returns the total fill size
     *
     * @return the total fill size
     */
    override fun watchFillSize(): Observable<Long> = fillSize

    /**
     * Returns the total storage size
     *
     * @return the total storage size
     */
    override fun getTotalStorageSize(): Single<Long> {
        return Single.fromCallable {
            val statFs = StatFs(getDirectory())
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) { //API 18
                val blockSize = statFs.blockSize.toLong()
                statFs.blockCount * blockSize
            } else {
                statFs.totalBytes
            }
        }
    }

    /**
     * Computes the fill size. This is a slow operation as it walks through the directory tree
     */
    private fun computeFillSize(): Long {
        val file = File(getDirectory())
        val dirs = LinkedList<File>()
        dirs.add(file)

        var result: Long = 0
        while (!dirs.isEmpty()) {
            val dir = dirs.removeAt(0)
            if (!dir.exists())
                continue
            val listFiles = dir.listFiles()
            if (listFiles == null || listFiles.isEmpty())
                continue
            for (child in listFiles) {
                result += child.length()
                if (child.isDirectory)
                    dirs.add(child)
            }
        }
        return result
    }

    /**
     * Returns the inherent directory
     *
     * @return the inherent directory
     */
    override fun getDirectory(): String = context.filesDir.absolutePath

    /**
     * Starts the fill operation
     *
     * @param limit the target fill size
     */
    override fun startFill(limit: Long): Observable<Double> {
        val initialFileCount = getFileCount()
        return Observable.interval(10, TimeUnit.MILLISECONDS)
            .map { initialFileCount + it }
            .switchMapSingle {
                if (limit == 0L) freeFill(it) else Single.just(0.0)
            }
            .onErrorResumeNext(Observable.just(0.0))
            .doOnSubscribe { notificationManager.showNotification() }
            .doFinally { notificationManager.cancelNotification() }
    }

    /**
     * Duplicate an asset that fits with the available storage size as much as possible
     */
    private fun freeFill(index: Long): Single<Double> {
        return Single.fromCallable {
            val start = System.nanoTime()
            val currentAsset = assetManager.getAsset(internalGetAvailableStorageSize())
            copyAssetsFile(currentAsset!!, currentAsset + index)
            val timeElapsed = System.nanoTime() - start
            val assetSize = assetManager.getAssetSize(currentAsset)
            fillSize.onNext(fillSize.blockingFirst() + assetSize)
            assetSize / timeElapsed.toDouble() * Constants.SPEED_FACTOR
        }
    }

    /**
     * Copy an asset file
     */
    private fun copyAssetsFile(assetsFileName: String, outputFileName: String) {
        val outputPath = getDirectory()
        File(outputPath).mkdirs()
        context.assets.open(assetsFileName).use { input ->
            FileOutputStream(File(outputPath, outputFileName)).use { out ->
                val data = ByteArray(4096)
                var count = input.read(data)
                while (count != -1) {
                    out.write(data, 0, count)
                    count = input.read(data)
                }
                out.flush()
                out.fd.sync()
            }
        }
    }

    /**
     * Returns the number of files in the target directory
     */
    private fun getFileCount(): Int = context.filesDir.listFiles()?.size ?: 0

    /**
     * Deletes all the fillers
     */
    override fun clearFill() {
        val list = context.filesDir?.listFiles() ?: arrayOf()
        list.forEach { it.delete() }
        fillSize.onNext(computeFillSize())
    }

    /**
     * Invalidates all information
     */
    override fun invalidate() {
        //TODO
    }
}
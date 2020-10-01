package com.tompee.utilities.filldevicespace.core.storage

import android.content.Context
import android.os.Build
import android.os.StatFs
import com.tompee.utilities.filldevicespace.di.qualifiers.FromApplication
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject
import java.io.File
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages disk state and information.
 *
 * Always use this class to access storage as this class does some precomputations to speed up
 * size computations
 */
@Singleton
internal class DiskStorageManager @Inject constructor(@FromApplication private val context: Context) :
    StorageManager {

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
        return Observable.fromCallable { // TODO: must observe fill as well
            val statFs = StatFs(getDirectory())
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) { //API 18
                val blockSize = statFs.blockSize.toLong()
                statFs.availableBlocks * blockSize
            } else {
                statFs.availableBytes
            }
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
     * Invalidates all information
     */
    override fun invalidate() {
        //TODO
    }
}
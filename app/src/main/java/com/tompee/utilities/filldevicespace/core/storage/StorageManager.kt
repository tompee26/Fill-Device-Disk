package com.tompee.utilities.filldevicespace.core.storage

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Environment
import android.os.StatFs
import com.tompee.utilities.filldevicespace.Constants
import java.io.File
import java.util.*

class StorageManager(private val context: Context, private val sharedPreferences: SharedPreferences) {

    fun getAvailableStorageSize(): Long {
        val statFs = StatFs(getFilesDirectory())
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) { //API 18
            val blockSize = statFs.blockSize.toLong()
            statFs.availableBlocks * blockSize
        } else {
            statFs.availableBytes
        }
    }

    fun getFillSize(): Long {
        val file = File(getFilesDirectory())

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

    fun getTotalStorageSize(): Long {
        val statFs = StatFs(getFilesDirectory())
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) { //API 18
            val blockSize = statFs.blockSize.toLong()
            statFs.blockCount * blockSize
        } else {
            statFs.totalBytes
        }
    }

    fun deleteFiles() {
        val list: Array<File> = if (sharedPreferences.getBoolean(Constants.TAG_SD_CARD, false)) {
            File(getRemovableStorage()).listFiles()
        } else {
            context.filesDir.listFiles()
        }
        for (file in list) {
            file.delete()
        }
    }

    fun getFileCount(): Int {
        return if (sharedPreferences.getBoolean(Constants.TAG_SD_CARD, false)) {
            File(getRemovableStorage()).listFiles().size
        } else context.filesDir.listFiles().size
    }

    private fun getFilesDirectory(): String? {
        return if (sharedPreferences.getBoolean(Constants.TAG_SD_CARD, false)) {
            getRemovableStorage()
        } else context.filesDir.absolutePath
    }

    private fun getRemovableStorage(): String? {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            val secondaryStorage = System.getenv(Constants.TAG_SECONDARY_STORAGE)
            if (secondaryStorage != null) {
                val paths = secondaryStorage.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                for (path in paths) {
                    val file = File(path)
                    if (file.listFiles() != null) {
                        val newPath = path + context.getExternalFilesDir(null)!!
                                .absolutePath.replace(Environment.getExternalStorageDirectory()
                                .absolutePath, "")
                        val fileInstance = File(newPath)
                        if (!fileInstance.exists()) {
                            if (!fileInstance.mkdirs()) {
                                return null
                            }
                        }
                        return newPath
                    }
                }
            }
        } else {
            val extDirs = context.getExternalFilesDirs(null)
            val primary = Environment.getExternalStorageDirectory()
            for (file in extDirs) {
                if (file == null) {
                    continue
                }
                if (!file.exists()) {
                    continue
                }
                val extFilePath = file.absolutePath

                if (extFilePath == null || extFilePath.isEmpty()) {
                    continue
                }
                if (extFilePath.startsWith(primary.absolutePath)) {
                    continue
                }
                // checks for API 19 and up only since a delay is added for lower APIs in BroadcastReceiver
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if (Environment.MEDIA_MOUNTED != Environment.getExternalStorageState(file)) {
                        continue
                    }
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    if (Environment.MEDIA_MOUNTED != Environment.getStorageState(file)) {
                        continue
                    }
                }
                return extFilePath
            }
        }
        return null
    }
}
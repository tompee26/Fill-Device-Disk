package com.tompee.utilities.filldevicespace.core.asset

import android.content.Context
import android.graphics.drawable.Drawable
import com.tompee.utilities.filldevicespace.core.storage.StorageManager
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader


class AssetManager(private val context: Context,
                   private val storageManager: StorageManager) {
    companion object {
        private const val ASSET_5MB = "filler_5MB"
        private const val ASSET_1MB = "filler_1MB"
        private const val ASSET_100KB = "filler_100KB"
        private const val ASSET_8KB = "filler_8KB"
        private const val ASSET_5MB_SIZE = 5242880L
        private const val ASSET_1MB_SIZE = 1048576L
        private const val ASSET_100KB_SIZE = 102400L
        private const val ASSET_8KB_SIZE = 8192L
    }

    private val mAssetMap: MutableMap<String, Long> = linkedMapOf(
            ASSET_5MB to ASSET_5MB_SIZE,
            ASSET_1MB to ASSET_1MB_SIZE,
            ASSET_100KB to ASSET_100KB_SIZE,
            ASSET_8KB to ASSET_8KB_SIZE
    )

    fun getAssetSize(asset: String): Long? {
        return mAssetMap[asset]
    }

    fun getAsset(limit: Long): String? {
        mAssetMap.entries.forEach {
            if (it.value <= limit) {
                return it.key
            }
        }
        return null
    }

    @Throws(IOException::class)
    fun copyAssetsFile(assetsFileName: String,
                       outputFileName: String) {
        val inputStream = context.assets.open(assetsFileName)
        val outputPath = storageManager.getFilesDirectory()

        java.io.File(outputPath).mkdirs()
        val outputStream = FileOutputStream(File(outputPath, outputFileName))
        val data = ByteArray(4096)
        var count = inputStream.read(data)
        while (count != -1) {
            outputStream.write(data, 0, count)
            count = inputStream.read(data)
        }
        outputStream.flush()
        outputStream.fd.sync()
        outputStream.close()
        inputStream.close()
    }

    fun getStringFromAsset(filename: String): String {
        val buffer = StringBuilder()
        val inputStream: InputStream
        try {
            inputStream = context.assets.open(filename)
            val bufferedReader = BufferedReader(InputStreamReader(inputStream, "UTF-8"))
            var str = bufferedReader.readLine()
            while (str != null) {
                buffer.append(str)
                str = bufferedReader.readLine()
            }
            bufferedReader.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return buffer.toString()
    }

    fun getDrawableFromAsset(filename: String): Drawable? {
        return try {
            val ims = context.assets.open(filename)
            Drawable.createFromStream(ims, null)
        } catch (ex: IOException) {
            null
        }
    }
}
package com.tompee.utilities.filldevicespace.core.asset

import android.content.Context
import android.graphics.drawable.Drawable
import com.tompee.utilities.filldevicespace.di.qualifiers.FromApplication
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Asset manager
 * Provides methods to access and load assets
 */
@Singleton
internal class AssetManager @Inject constructor(
    @FromApplication private val context: Context
) {
    companion object {
        private const val ASSET_5MB = "filler_5MB.dat"
        private const val ASSET_1MB = "filler_1MB.dat"
        private const val ASSET_100KB = "filler_100KB.dat"
        private const val ASSET_8KB = "filler_8KB.dat"
        private const val ASSET_5MB_SIZE = 5242880L
        private const val ASSET_1MB_SIZE = 1048576L
        private const val ASSET_100KB_SIZE = 102400L
        private const val ASSET_8KB_SIZE = 8192L
    }

    /**
     * Mapping between asset and its inherent size
     */
    private val assetMap = mapOf(
        ASSET_5MB to ASSET_5MB_SIZE,
        ASSET_1MB to ASSET_1MB_SIZE,
        ASSET_100KB to ASSET_100KB_SIZE,
        ASSET_8KB to ASSET_8KB_SIZE
    )

    /**
     * Returns an asset that is less than or equal to the given [limit]
     *
     * @param limit the target limit
     * @return returns an asset that is less than or equal to the given [limit]
     */
    fun getAsset(limit: Long): String? {
        return assetMap.entries.firstOrNull { it.value <= limit }?.key
    }

    /**
     * Returns the corresponding size of the given [asset]
     *
     * @param asset the asset name
     * @return the corresponding size of the given [asset]
     */
    fun getAssetSize(asset: String): Long {
        return assetMap[asset] ?: throw IllegalArgumentException("Asset not found")
    }

    /**
     * Returns a drawable from a given asset
     *
     * @param filename asset file name
     * @return a drawable from a given asset
     */
    fun getDrawable(filename: String): Drawable {
        val ims = context.assets.open(filename)
        return Drawable.createFromStream(ims, null)
    }

    /**
     * Returns a string from a given asset
     *
     * @param filename asset file name
     * @return a string from a given asset
     */
    fun getText(filename: String): String {
        val buffer = StringBuilder()
        val inputStream: InputStream = context.assets.open(filename)
        BufferedReader(InputStreamReader(inputStream, "UTF-8")).use {
            var str = it.readLine()
            while (str != null) {
                buffer.append(str)
                str = it.readLine()
            }
        }
        return buffer.toString()
    }
}
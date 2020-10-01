package com.tompee.utilities.filldevicespace.common.extensions

import android.content.Context
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat

/**
 * Resolves the color independent of the build version
 */
internal fun Context.resolveColor(@ColorRes color: Int): Int {
    return ContextCompat.getColor(this, color)
}
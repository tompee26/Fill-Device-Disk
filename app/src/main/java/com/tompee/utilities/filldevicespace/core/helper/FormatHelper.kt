package com.tompee.utilities.filldevicespace.core.helper

import android.content.Context
import android.text.format.Formatter
import com.tompee.utilities.filldevicespace.R

class FormatHelper(private val context: Context) {
    fun formatFileSize(size: Long): String =
            Formatter.formatFileSize(context, size)

    fun formatSpeed(speed: Double): String = String.format(context.getString(R.string.ids_legend_speed_unit), speed)
}
package com.tompee.utilities.filldevicespace.core.helper

import android.content.Context
import android.support.annotation.ColorRes
import android.support.annotation.StringRes
import android.support.v4.content.ContextCompat
import android.text.format.Formatter
import com.tompee.utilities.filldevicespace.R

class FormatHelper(private val context: Context) {
    fun formatFileSize(size: Long): String =
            Formatter.formatFileSize(context, size)

    fun formatSpeed(speed: Double): String = String.format(context.getString(R.string.ids_legend_speed_unit), speed)

    fun getString(@StringRes id: Int): String = context.getString(id)

    fun getColor(@ColorRes id: Int): Int = ContextCompat.getColor(context, id)
}
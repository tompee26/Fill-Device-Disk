package com.tompee.utilities.filldevicespace.core.helpers

import android.content.Context
import android.text.format.Formatter
import com.tompee.utilities.filldevicespace.R
import com.tompee.utilities.filldevicespace.di.qualifiers.FromApplication
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class FormatHelper @Inject constructor(@FromApplication private val context: Context) {
    fun formatFileSize(size: Long): String = Formatter.formatFileSize(context, size)

    fun formatSpeed(speed: Double): String =
        String.format(context.getString(R.string.ids_legend_speed_unit), speed)
}
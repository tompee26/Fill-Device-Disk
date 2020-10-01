package com.tompee.utilities.filldevicespace.fill.storage

import android.content.Context
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.jakewharton.rx.replayingShare
import com.tompee.utilities.filldevicespace.R
import com.tompee.utilities.filldevicespace.common.extensions.resolveColor
import com.tompee.utilities.filldevicespace.common.ui.BaseViewModel
import com.tompee.utilities.filldevicespace.core.helpers.FormatHelper
import com.tompee.utilities.filldevicespace.core.storage.DiskStorageManager
import com.tompee.utilities.filldevicespace.di.qualifiers.FromApplication
import io.reactivex.rxkotlin.Observables
import javax.inject.Inject

/**
 * Storage view model
 * Communicates with the core to update storage information
 */
internal class StorageViewModel @Inject constructor(
    @FromApplication private val context: Context,
    private val storageManager: DiskStorageManager,
    private val formatHelper: FormatHelper
) : BaseViewModel() {

    private val sharedFillSize = storageManager.watchFillSize()
        .replayingShare()

    private val sharedFreeSize = storageManager.getAvailableStorageSize()
        .replayingShare()

    /**
     * Emits the current fill size
     */
    val fillSize = sharedFillSize
        .map(formatHelper::formatFileSize)
        .toLiveData()

    /**
     * Emits the current available storage size
     */
    val freeSize = sharedFreeSize
        .map(formatHelper::formatFileSize)
        .toLiveData()

    /**
     * Emits the storage space occupied by the system
     */
    val systemSize = Observables.combineLatest(
        storageManager.getTotalStorageSize().toObservable(),
        sharedFillSize, sharedFreeSize
    ) { total, fill, free -> total - fill - free }
        .map(formatHelper::formatFileSize)
        .toLiveData()

    /**
     * Emits the chart data
     */
    val chartData = Observables.combineLatest(
        sharedFreeSize, sharedFillSize, storageManager.getTotalStorageSize().toObservable()
    ) { free, fill, total -> createPieData(free, fill, total) }
        .toLiveData()

    /**
     * Invalidates the information
     */
    fun invalidate() = storageManager.invalidate()

    private fun createPieData(free: Long, fill: Long, total: Long): PieData {
        val entries = listOfNotNull(
            if (fill != 0L) {
                PieEntry(
                    fill.toFloat() / total.toFloat() * 100,
                    context.getString(R.string.ids_legend_fill)
                )
            } else null,
            if (free != 0L) {
                PieEntry(
                    free.toFloat() / total.toFloat() * 100,
                    context.getString(R.string.ids_legend_free)
                )
            } else null,
            PieEntry(
                (total - free - fill).toFloat() / total.toFloat() * 100,
                context.getString(R.string.ids_legend_system)
            )
        )

        val colorList = listOfNotNull(
            if (fill != 0L) context.resolveColor(R.color.colorAccent) else null,
            if (free != 0L) context.resolveColor(R.color.colorPrimaryLight) else null,
            context.resolveColor(R.color.colorLightDisabled)
        )
        val dataSet = PieDataSet(entries, context.getString(R.string.ids_legend_storage))
            .apply {
                sliceSpace = 3f
                selectionShift = 5f
                colors = colorList
            }

        return PieData(dataSet).apply {
            setValueFormatter(PercentFormatter())
            setValueTextSize(11f)
            setDrawValues(false)
        }
    }
}
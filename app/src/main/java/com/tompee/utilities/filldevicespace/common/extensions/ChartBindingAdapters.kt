package com.tompee.utilities.filldevicespace.common.extensions

import androidx.databinding.BindingAdapter
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData

/**
 * Sets a new data to the Pie chart
 */
@BindingAdapter("pieData")
internal fun PieChart.setBindingData(data: PieData?) {
    if (data != null) {
        this.data = data
        highlightValues(null)
        invalidate()
    }
}
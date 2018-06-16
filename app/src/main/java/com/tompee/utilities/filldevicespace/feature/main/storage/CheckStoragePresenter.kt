package com.tompee.utilities.filldevicespace.feature.main.storage

import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.tompee.utilities.filldevicespace.R
import com.tompee.utilities.filldevicespace.base.BasePresenter
import com.tompee.utilities.filldevicespace.core.helper.FormatHelper
import com.tompee.utilities.filldevicespace.interactor.FillInteractor
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function3

class CheckStoragePresenter(private val fillInteractor: FillInteractor,
                            private val formatHelper: FormatHelper) : BasePresenter<CheckStorageView>() {

    override fun onAttachView() {
        setupFreeSpaceTracker()
        setupFillSpaceTracker()
        setupTotalSpaceTracker()
        setupDataListener()
    }

    override fun onDetachView() {
    }

    private fun setupFreeSpaceTracker() {
        addSubscription(
                Observable.combineLatest(fillInteractor.getFreeSpaceObservable(),
                        fillInteractor.getFillSpaceObservable(),
                        fillInteractor.getMaxStorageSpaceObservable(),
                        Function3<Long, Long, Long, Long> { free, fill, max -> max - free - fill })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe {
                            view.setFreeSpace(formatHelper.formatFileSize(it))
                        })
    }

    private fun setupFillSpaceTracker() {
        addSubscription(fillInteractor.getFillSpaceObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    view.setFillSpace(formatHelper.formatFileSize(it))
                })
    }

    private fun setupTotalSpaceTracker() {
        addSubscription(fillInteractor.getMaxStorageSpaceObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    view.setMaxSpace(formatHelper.formatFileSize(it))
                })
    }

    private fun setupDataListener() {
        val disposable = Observable.combineLatest(fillInteractor.getFreeSpaceObservable(),
                fillInteractor.getFillSpaceObservable(),
                fillInteractor.getMaxStorageSpaceObservable(),
                Function3(this::createPieData))
                .map(view::setData)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
        addSubscription(disposable)
    }

    private fun createPieData(free: Long, fill: Long, total: Long): PieData {
        val entries = ArrayList<PieEntry>()
        if (fill != 0L) {
            entries.add(PieEntry(fill.toFloat() / total.toFloat() * 100, formatHelper.getString(R.string.ids_legend_fill)))
        }
        if (free != 0L) {
            entries.add(PieEntry(free.toFloat() / total.toFloat() * 100, formatHelper.getString(R.string.ids_legend_free)))
        }
        entries.add(PieEntry((total - free - fill).toFloat() / total.toFloat() * 100, formatHelper.getString(R.string.ids_legend_system)))

        val dataSet = PieDataSet(entries, formatHelper.getString(R.string.ids_legend_storage))
        dataSet.sliceSpace = 3f
        dataSet.selectionShift = 5f

        val colors = ArrayList<Int>()
        if (fill != 0L) {
            colors.add(formatHelper.getColor(R.color.colorAccent))
        }
        if (free != 0L) {
            colors.add(formatHelper.getColor(R.color.colorPrimaryLight))
        }
        colors.add(formatHelper.getColor(R.color.light_text_disable))
        dataSet.colors = colors

        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter())
        data.setValueTextSize(11f)
        data.setDrawValues(false)
        return data
    }
}
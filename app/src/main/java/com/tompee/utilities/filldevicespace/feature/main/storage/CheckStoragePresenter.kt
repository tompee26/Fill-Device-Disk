package com.tompee.utilities.filldevicespace.feature.main.storage

import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.tompee.utilities.filldevicespace.R
import com.tompee.utilities.filldevicespace.common.BasePresenter
import com.tompee.utilities.filldevicespace.core.helper.ContentHelper
import com.tompee.utilities.filldevicespace.core.helper.FormatHelper
import com.tompee.utilities.filldevicespace.interactor.FillInteractor
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.Observables
import io.reactivex.schedulers.Schedulers

class CheckStoragePresenter(private val fillInteractor: FillInteractor,
                            private val formatHelper: FormatHelper,
                            private val contentHelper: ContentHelper) : BasePresenter<CheckStorageView>() {

    override fun onAttachView() {
        setupRefresh()
        setupFreeSpaceTracker()
        setupFillSpaceTracker()
        setupSystemSpaceTracker()
        setupDataListener()
        setupSdCard()
    }

    override fun onDetachView() {
    }

    private fun setupRefresh() {
        addSubscription(view.refreshObservable()
                .doOnNext { fillInteractor.refresh() }
                .subscribe())
    }

    private fun setupFreeSpaceTracker() {
        addSubscription(fillInteractor.getFreeSpaceObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { view.setFreeSpace(formatHelper.formatFileSize(it)) })
    }

    private fun setupFillSpaceTracker() {
        addSubscription(fillInteractor.getFillSpaceObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { view.setFillSpace(formatHelper.formatFileSize(it)) })
    }

    private fun setupSystemSpaceTracker() {
        addSubscription(Observables.combineLatest(fillInteractor.getFreeSpaceObservable(),
                fillInteractor.getFillSpaceObservable(),
                fillInteractor.getMaxStorageSpaceObservable()) { free, fill, max -> max - free - fill }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { view.setMaxSpace(formatHelper.formatFileSize(it)) })
    }

    private fun setupDataListener() {
        addSubscription(Observables.zip(fillInteractor.getFreeSpaceObservable(),
                fillInteractor.getFillSpaceObservable(),
                fillInteractor.getMaxStorageSpaceObservable(), this::createPieData)
                .observeOn(AndroidSchedulers.mainThread())
                .map(view::setData)
                .subscribe())
    }

    private fun createPieData(free: Long, fill: Long, total: Long): PieData {
        val entries = ArrayList<PieEntry>()
        if (fill != 0L) {
            entries.add(PieEntry(fill.toFloat() / total.toFloat() * 100, contentHelper.getString(R.string.ids_legend_fill)))
        }
        if (free != 0L) {
            entries.add(PieEntry(free.toFloat() / total.toFloat() * 100, contentHelper.getString(R.string.ids_legend_free)))
        }
        entries.add(PieEntry((total - free - fill).toFloat() / total.toFloat() * 100, contentHelper.getString(R.string.ids_legend_system)))

        val dataSet = PieDataSet(entries, contentHelper.getString(R.string.ids_legend_storage))
        dataSet.sliceSpace = 3f
        dataSet.selectionShift = 5f

        val colors = ArrayList<Int>()
        if (fill != 0L) {
            colors.add(contentHelper.getColor(R.color.colorAccent))
        }
        if (free != 0L) {
            colors.add(contentHelper.getColor(R.color.colorPrimaryLight))
        }
        colors.add(contentHelper.getColor(R.color.light_text_disable))
        dataSet.colors = colors

        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter())
        data.setValueTextSize(11f)
        data.setDrawValues(false)
        return data
    }

    private fun setupSdCard() {
        view.setSdCardButtonState(fillInteractor.isRemovableStorageSupported())
        addSubscription(fillInteractor.getSdCardEnabledObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { state ->
                    val color = if (state) {
                        contentHelper.getColor(R.color.tabSelected)
                    } else {
                        contentHelper.getColor(android.R.color.transparent)
                    }
                    view.setSdCardButtonBackground(color)
                })
        addSubscription(view.sdCardObservable()
                .observeOn(Schedulers.io())
                .doOnNext {
                    fillInteractor.toggleSdCard()
                }
                .subscribe())
    }
}
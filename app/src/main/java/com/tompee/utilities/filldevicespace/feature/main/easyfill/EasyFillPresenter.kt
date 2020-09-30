package com.tompee.utilities.filldevicespace.feature.main.easyfill

import com.tompee.utilities.filldevicespace.R
import com.tompee.utilities.filldevicespace.common.BasePresenter
import com.tompee.utilities.filldevicespace.core.helper.ContentHelper
import com.tompee.utilities.filldevicespace.core.helper.FormatHelper
import com.tompee.utilities.filldevicespace.interactor.FillInteractor
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class EasyFillPresenter(private val fillInteractor: FillInteractor,
                        private val formatHelper: FormatHelper,
                        private val contentHelper: ContentHelper) : BasePresenter<EasyFillView>() {
    private var fillSubscription: Disposable? = null

    override fun onAttachView() {
        setupFreeSpaceTracker()
        setupFillSpaceTracker()
        setupPercentageTracker()
        setupSpeedTracker()
        setupFill()
        setupClear()
        setupSdCard()
    }

    override fun onDetachView() {
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

    private fun setupPercentageTracker() {
        addSubscription(fillInteractor.getPercentageObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(view::setPercentage))
    }

    private fun setupSpeedTracker() {
        addSubscription(fillInteractor.getSpeedObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { view.setSpeed(formatHelper.formatSpeed(it)) })
    }

    private fun setupFill() {
        addSubscription(view.startObservable()
                .map {
                    fillSubscription = if (fillSubscription != null) {
                        fillSubscription?.dispose()
                        null
                    } else {
                        fillInteractor.startFill()
                                .subscribeOn(Schedulers.computation())
                                .observeOn(AndroidSchedulers.mainThread())
                                .doOnComplete {
                                    fillSubscription = null
                                    view.setFillState(false, fillInteractor.isRemovableStorageSupported())
                                }
                                .subscribe()
                    }
                    return@map fillSubscription != null
                }
                .doOnNext { state -> view.setFillState(state, fillInteractor.isRemovableStorageSupported()) }
                .subscribe())
    }

    private fun setupClear() {
        addSubscription(view.clearObservable()
                .observeOn(Schedulers.computation())
                .map { fillInteractor.clearFill() }
                .subscribe())
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
                .doOnNext { fillInteractor.toggleSdCard() }
                .subscribe())
    }
}
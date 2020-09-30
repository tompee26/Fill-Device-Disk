package com.tompee.utilities.filldevicespace.feature.main.advancefill

import com.tompee.utilities.filldevicespace.R
import com.tompee.utilities.filldevicespace.common.BasePresenter
import com.tompee.utilities.filldevicespace.core.helper.ContentHelper
import com.tompee.utilities.filldevicespace.core.helper.FormatHelper
import com.tompee.utilities.filldevicespace.interactor.FillInteractor
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.withLatestFrom
import io.reactivex.schedulers.Schedulers

class AdvanceFillPresenter(private val fillInteractor: FillInteractor,
                           private val formatHelper: FormatHelper,
                           private val contentHelper: ContentHelper) : BasePresenter<AdvanceFillView>() {
    private var fillSubscription: Disposable? = null

    override fun onAttachView() {
        setupFreeSpaceTracker()
        setupFillSpaceTracker()
        setupPercentageTracker()
        setupSpeedTracker()
        setupFill()
        setupClear()
        setupDialLimit()
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
                .withLatestFrom(Observables.combineLatest(view.getGbObservable(), view.getMbObservable()) { gb, mb ->
                    val mbValue = mb.toLong() * 1000000L
                    val gbValue = gb.toLong() * 1000000000L
                    mbValue + gbValue
                }) { _, lmt -> lmt }
                .map {
                    fillSubscription = if (fillSubscription != null) {
                        fillSubscription?.dispose()
                        null
                    } else {
                        fillInteractor.startFill(it)
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

    private fun setupDialLimit() {
        addSubscription(Observables.combineLatest(view.getGbObservable(), view.getMbObservable(),
                fillInteractor.getMaxStorageSpaceObservable()) { gb, mb, total ->
            val mbValue = mb.toLong() * 1048576L
            val gbValue = gb.toLong() * 1073741824L
            val sum = mbValue + gbValue
            sum <= total && sum != 0L
        }.doOnNext(view::setStartButtonState)
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
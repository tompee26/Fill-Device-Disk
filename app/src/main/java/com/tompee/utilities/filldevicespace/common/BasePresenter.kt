package com.tompee.utilities.filldevicespace.common

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class BasePresenter<T : BaseMvpView> {
    private val compositeDisposable = CompositeDisposable()
    protected lateinit var view: T

    fun attachView(mvpView: T) {
        view = mvpView
        onAttachView()
    }

    fun detachView() {
        compositeDisposable.clear()
        onDetachView()
    }

    protected fun addSubscription(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }

    abstract fun onDetachView()

    abstract fun onAttachView()
}
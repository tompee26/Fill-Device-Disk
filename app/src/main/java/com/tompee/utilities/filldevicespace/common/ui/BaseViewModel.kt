package com.tompee.utilities.filldevicespace.common.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import timber.log.Timber

/**
 * Base view model.
 * Provides support for event live data. Also provides convenience functions for event live data
 * construction, live data observation and conversion from Rx to LiveData
 */
internal abstract class BaseViewModel : ViewModel() {

    /**
     * Represents an event. Events are single use data containers that are automatically
     * set to null after the value is resolved once
     */
    class Event<out T>(private val event: T) {

        private var handled = false

        fun get(): T? {
            return if (handled) null else {
                handled = true
                event
            }
        }
    }

    /**
     * A wrapper around [MutableLiveData] that uses [Event]
     */
    class EventLiveData<T> : MutableLiveData<Event<T>> {

        constructor(defaultValue: T) : super(Event(defaultValue))
        constructor() : super()

        @JvmName("eventPostValue")
        fun postValue(data: T) = postValue(Event(data))

        @JvmName("eventSetValue")
        fun settValue(data: T) = apply { value = (Event(data)) }
    }

    /**
     * Contains all the Rx subscriptions
     */
    protected val subscriptions = CompositeDisposable()

    /**
     * Converts an observable into a live data
     */
    protected fun <T> Observable<T>.toLiveData(): LiveData<T> {
        return MutableLiveData<T>().also { liveData ->
            subscriptions += this.subscribe({ liveData.postValue(it) }, Timber::e)
        }
    }

    /**
     * Converts an observable into a live data
     */
    protected fun <T> Observable<T>.toLiveData(defaultValue: T): LiveData<T> {
        return MutableLiveData(defaultValue).also { liveData ->
            subscriptions += this.subscribe({ liveData.postValue(it) }, Timber::e)
        }
    }

    /**
     * Converts an observable into an event live data
     */
    protected fun <T> Observable<T>.toEventLiveData(): EventLiveData<T> {
        return EventLiveData<T>().also { liveData ->
            subscriptions += this.subscribe({ liveData.postValue(it) }, Timber::e)
        }
    }

    /**
     * Converts an observable into an event live data
     */
    protected fun <T> Observable<T>.toEventLiveData(defaultValue: T): EventLiveData<T> {
        return EventLiveData(defaultValue).also { liveData ->
            subscriptions += this.subscribe({ liveData.postValue(it) }, Timber::e)
        }
    }

    /**
     * Converts a single into a live data
     */
    protected fun <T> Single<T>.toLiveData(): LiveData<T> {
        return MutableLiveData<T>().also { liveData ->
            subscriptions += this.subscribe({ liveData.postValue(it) }, Timber::e)
        }
    }

    /**
     * Converts a single into a live data
     */
    protected fun <T> Single<T>.toLiveData(defaultValue: T): LiveData<T> {
        return MutableLiveData(defaultValue).also { liveData ->
            subscriptions += this.subscribe({ liveData.postValue(it) }, Timber::e)
        }
    }

    /**
     * Converts a single into a live data
     */
    protected fun <T> Single<T>.toEventLiveData(): EventLiveData<T> {
        return EventLiveData<T>().also { liveData ->
            subscriptions += this.subscribe({ liveData.postValue(it) }, Timber::e)
        }
    }

    /**
     * Converts a single into a live data
     */
    protected fun <T> Single<T>.toEventLiveData(defaultValue: T): EventLiveData<T> {
        return EventLiveData(defaultValue).also { liveData ->
            subscriptions += this.subscribe({ liveData.postValue(it) }, Timber::e)
        }
    }

    /**
     * Converts a flowable into a live data
     */
    protected fun <T> Flowable<T>.toLiveData(): LiveData<T> {
        return MutableLiveData<T>().also { liveData ->
            subscriptions += this.subscribe({ liveData.postValue(it) }, Timber::e)
        }
    }

    /**
     * Converts a flowable into a live data with initial value
     */
    protected fun <T> Flowable<T>.toLiveData(defaultValue: T): LiveData<T> {
        return MutableLiveData(defaultValue).also { liveData ->
            subscriptions += this.subscribe({ liveData.postValue(it) }, Timber::e)
        }
    }

    /**
     * Converts a flowable into a live data
     */
    protected fun <T> Flowable<T>.toEventLiveData(): EventLiveData<T> {
        return EventLiveData<T>().also { liveData ->
            subscriptions += this.subscribe({ liveData.postValue(it) }, Timber::e)
        }
    }

    /**
     * Converts a flowable into a live data with initial value
     */
    protected fun <T> Flowable<T>.toEventLiveData(defaultValue: T): EventLiveData<T> {
        return EventLiveData(defaultValue).also { liveData ->
            subscriptions += this.subscribe({ liveData.postValue(it) }, Timber::e)
        }
    }

    public override fun onCleared() {
        super.onCleared()
        subscriptions.clear()
    }
}
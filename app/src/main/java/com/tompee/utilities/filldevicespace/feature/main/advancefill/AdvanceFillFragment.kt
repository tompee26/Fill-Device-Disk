package com.tompee.utilities.filldevicespace.feature.main.advancefill

import android.content.res.ColorStateList
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.View
import com.jakewharton.rxbinding2.view.RxView
import com.tompee.utilities.filldevicespace.FillDeviceDiskApp
import com.tompee.utilities.filldevicespace.R
import com.tompee.utilities.filldevicespace.base.BaseFragment
import com.tompee.utilities.filldevicespace.di.component.DaggerMainComponent
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.fragment_advance_fill.*
import kotlinx.android.synthetic.main.view_custom_dial.*
import javax.inject.Inject

class AdvanceFillFragment : BaseFragment(), AdvanceFillView {
    @Inject
    lateinit var presenter: AdvanceFillPresenter

    //region Lifecycle
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        start.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context!!, R.color.colorAccentLight))
        start.setImageResource(R.drawable.ic_play_arrow_white)
        presenter.attachView(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }
    //endregion

    //region BaseFragment
    override fun setupComponent() {
        val component = DaggerMainComponent.builder()
                .appComponent((activity?.application as FillDeviceDiskApp).component)
                .build()
        component.inject(this)
    }

    override fun layoutId(): Int = R.layout.fragment_advance_fill
    //endregion

    //region View
    override fun startObservable(): Observable<Any> = RxView.clicks(start)

    override fun clearObservable(): Observable<Any> = RxView.clicks(clearFill)

    override fun getMbObservable(): Observable<Int> {
        val subject = BehaviorSubject.create<Int>()
        subject.onNext(megabytes.currentValue.toInt())
        megabytes.setOnProgressChangedListener {
            subject.onNext(it.toInt())
        }
        return subject
    }

    override fun getGbObservable(): Observable<Int> {
        val subject = BehaviorSubject.create<Int>()
        subject.onNext(gigabytes.currentValue.toInt())
        gigabytes.setOnProgressChangedListener {
            subject.onNext(it.toInt())
        }
        return subject
    }

    override fun setFreeSpace(space: String) {
        freeSpace.text = space
    }

    override fun setFillSpace(space: String) {
        fillSpace.text = space
    }

    override fun setPercentage(percentage: Double) {
        circleView.setValue(percentage.toFloat() * 100)
    }

    override fun setSpeed(text: String) {
        speed.text = text
    }

    override fun setFillState(state: Boolean) {
        clearFill.isEnabled = !state
        sdCard.isEnabled = !state
        if (state) {
            start.setImageResource(R.drawable.ic_stop_white)
        } else {
            start.setImageResource(R.drawable.ic_play_arrow_white)
        }
        switcher.showNext()
    }

    override fun setStartButtonState(state: Boolean) {
        start.isEnabled = state
    }

    //endregion

}
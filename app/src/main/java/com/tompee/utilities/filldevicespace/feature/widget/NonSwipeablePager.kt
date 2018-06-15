package com.tompee.utilities.filldevicespace.feature.widget

import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.MotionEvent

class NonSwipeablePager(context: Context, attrs: AttributeSet) : ViewPager(context, attrs) {

    var isSwipeAllowed: Boolean = false

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return !isSwipeAllowed && super.onTouchEvent(event)
    }

    override fun onInterceptTouchEvent(event: MotionEvent?): Boolean {
        return !isSwipeAllowed && super.onInterceptTouchEvent(event)
    }
}
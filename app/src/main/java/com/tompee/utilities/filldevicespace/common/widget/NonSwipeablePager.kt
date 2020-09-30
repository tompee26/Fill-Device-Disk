package com.tompee.utilities.filldevicespace.common.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

/**
 * A non-swipeable view pager
 */
internal class NonSwipeablePager(context: Context, attrs: AttributeSet) :
    ViewPager(context, attrs) {

    var isSwipeAllowed: Boolean = false

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return !isSwipeAllowed && super.onTouchEvent(event)
    }

    override fun onInterceptTouchEvent(event: MotionEvent?): Boolean {
        return !isSwipeAllowed && super.onInterceptTouchEvent(event)
    }
}
package com.tompee.utilities.filldevicespace.view.custom;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class NonSwipeablePager extends ViewPager {
    private boolean mIsSwipeAllowed;

    public NonSwipeablePager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return !mIsSwipeAllowed && super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return !mIsSwipeAllowed && super.onInterceptTouchEvent(event);
    }

    public void interceptSwipeEvent(boolean intercept) {
        mIsSwipeAllowed = intercept;
    }
}

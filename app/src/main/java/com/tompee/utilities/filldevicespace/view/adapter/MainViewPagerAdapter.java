package com.tompee.utilities.filldevicespace.view.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.tompee.utilities.filldevicespace.R;
import com.tompee.utilities.filldevicespace.view.fragment.EasyFillFragment;

public class MainViewPagerAdapter extends FragmentPagerAdapter {
    private static final int PAGE_COUNT = 2;
    private final Context mContext;
    private final EasyFillFragment mEasyFillFragment;

    public MainViewPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
        mEasyFillFragment = EasyFillFragment.getInstance();
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        return EasyFillFragment.getInstance();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String name;
        switch (position) {
            case 0:
                name = mContext.getString(R.string.ids_lbl_easy_fill);
                break;
            default:
                name = mContext.getString(R.string.ids_lbl_advance_fill);
                break;
        }
        return name;
    }
}

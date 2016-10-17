package com.tompee.utilities.filldevicespace.view.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.tompee.utilities.filldevicespace.R;
import com.tompee.utilities.filldevicespace.view.fragment.AdvanceFillFragment;
import com.tompee.utilities.filldevicespace.view.fragment.CheckStorageFragment;
import com.tompee.utilities.filldevicespace.view.fragment.EasyFillFragment;

public class MainViewPagerAdapter extends FragmentPagerAdapter {
    private static final int PAGE_COUNT = 3;
    private final Context mContext;
    private final EasyFillFragment mEasyFillFragment;
    private final AdvanceFillFragment mAdvanceFillFragment;
    private final CheckStorageFragment mCheckStorageFragment;

    public MainViewPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
        mEasyFillFragment = EasyFillFragment.getInstance();
        mAdvanceFillFragment = AdvanceFillFragment.getInstance();
        mCheckStorageFragment = CheckStorageFragment.getInstance();
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 1:
                return mAdvanceFillFragment;
            case 2:
                return mCheckStorageFragment;
            default:
        }
        return mEasyFillFragment;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String name;
        switch (position) {
            case 0:
                name = mContext.getString(R.string.ids_lbl_easy_fill);
                break;
            case 1:
                name = mContext.getString(R.string.ids_lbl_advance_fill);
                break;
            default:
                name = mContext.getString(R.string.ids_lbl_check_storage);
                break;
        }
        return name;
    }
}

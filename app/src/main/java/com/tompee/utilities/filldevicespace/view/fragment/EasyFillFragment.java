package com.tompee.utilities.filldevicespace.view.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tompee.utilities.filldevicespace.R;
import com.tompee.utilities.filldevicespace.controller.storage.StorageUtility;
import com.tompee.utilities.filldevicespace.controller.task.ClearFillTask;
import com.tompee.utilities.filldevicespace.controller.task.FillDiskTask;
import com.tompee.utilities.filldevicespace.view.MainActivity;
import com.tompee.utilities.filldevicespace.view.base.BaseActivity;

import at.grabner.circleprogress.CircleProgressView;

public class EasyFillFragment extends Fragment implements FillDiskTask.FillDiskTaskListener,
        View.OnClickListener, ClearFillTask.ClearFillListener {
    private FillDiskTask mFillDiskTask;
    private TextView mFreeView;
    private TextView mFillView;
    private TextView mSpeedView;
    private View mClearFillView;
    private View mSdCardView;
    private CircleProgressView mCircleProgressView;
    private SharedPreferences mSharedPrefs;

    public static EasyFillFragment getInstance() {
        return new EasyFillFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSharedPrefs = getContext().getSharedPreferences(MainActivity.
                SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_easy_fill, container, false);
        FloatingActionButton startButton = (FloatingActionButton) view.findViewById(R.id.start);
        startButton.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.
                getColor(getContext(), R.color.colorAccentLight)));
        startButton.setImageResource(R.drawable.ic_play_arrow_white);
        startButton.setOnClickListener(this);

        mClearFillView = view.findViewById(R.id.clear_fill);
        mClearFillView.setOnClickListener(this);
        mSdCardView = view.findViewById(R.id.sd_card);
        mSdCardView.setOnClickListener(this);
        setSdCardState();

        mFreeView = (TextView) view.findViewById(R.id.free_space);
        mFillView = (TextView) view.findViewById(R.id.fill_space);
        mSpeedView = (TextView) view.findViewById(R.id.speed);
        mCircleProgressView = (CircleProgressView) view.findViewById(R.id.circleView);
        mCircleProgressView.setBarColor(ContextCompat.getColor(getContext(), R.color.colorAccentLight),
                ContextCompat.getColor(getContext(), R.color.colorAccent));
        updateViews(0.00f);
        return view;
    }

    private void updateViews(float speed) {
        if (mFreeView != null) {
            long free = StorageUtility.getAvailableStorageSize(getContext());
            mFreeView.setText(Formatter.formatFileSize(getContext(), free));
            mFillView.setText(Formatter.formatFileSize(getContext(), StorageUtility.
                    getFillSize(getContext())));
            mSpeedView.setText(String.format(getString(R.string.ids_legend_speed_unit), speed));
            long total = StorageUtility.getTotalStorageSize(getContext());
            float percentage = ((float) (total - free) / (float) total);
            mCircleProgressView.setValue(percentage * 100);
        }
    }

    @Override
    public void onFillDiskSpaceComplete() {
        updateViews(0.00f);
        mFillDiskTask = null;
        mClearFillView.setEnabled(true);
        setSdCardState();
        MainActivity activity = (MainActivity) getActivity();
        activity.interceptViewPagerTouchEvents(false);
    }

    @Override
    public void onProgressUpdate(float speed) {
        updateViews(speed);
    }

    @Override
    public void onCancelled() {
        updateViews(0.00f);
        mFillDiskTask = null;
        mClearFillView.setEnabled(true);
        setSdCardState();
        MainActivity activity = (MainActivity) getActivity();
        activity.interceptViewPagerTouchEvents(false);
    }

    @Override
    public void onClick(View view) {
        MainActivity activity = (MainActivity) getActivity();
        switch (view.getId()) {
            case R.id.start:
                FloatingActionButton fab = (FloatingActionButton) view;
                if (mFillDiskTask == null) {
                    mClearFillView.setEnabled(false);
                    activity.interceptViewPagerTouchEvents(true);
                    mSdCardView.setEnabled(false);
                    fab.setImageResource(R.drawable.ic_stop_white);
                    mFillDiskTask = new FillDiskTask(getContext(), this, 0);
                    mFillDiskTask.execute();
                } else {
                    fab.setImageResource(R.drawable.ic_play_arrow_white);
                    mFillDiskTask.cancel(true);
                }
                break;
            case R.id.clear_fill:
                activity.interceptTouchEvents(true);
                ClearFillTask task = new ClearFillTask(getContext(), this);
                task.execute();
                break;
            case R.id.sd_card:
                SharedPreferences.Editor editor = mSharedPrefs.edit();
                if (mSharedPrefs.getBoolean(MainActivity.TAG_SD_CARD, false)) {
                    editor.putBoolean(MainActivity.TAG_SD_CARD, false);
                } else {
                    editor.putBoolean(MainActivity.TAG_SD_CARD, true);
                }
                editor.apply();
                setSdCardState();
                updateViews(0.00f);
                break;
        }
    }

    private void setSdCardState() {
        if (StorageUtility.getRemovableStorage(getContext()) != null) {
            mSdCardView.setEnabled(true);
        } else {
            mSdCardView.setEnabled(false);
            SharedPreferences.Editor editor = mSharedPrefs.edit();
            editor.putBoolean(MainActivity.TAG_SD_CARD, false);
            editor.apply();
        }
        mSdCardView.setBackgroundColor(mSharedPrefs.getBoolean(MainActivity.TAG_SD_CARD, false) ?
                ContextCompat.getColor(getContext(), R.color.tabSelected) : ContextCompat.
                getColor(getContext(), android.R.color.transparent));
    }

    @Override
    public void onFinish() {
        ((BaseActivity) getActivity()).interceptTouchEvents(false);
        updateViews(0.00f);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        updateViews(0.00f);
    }
}

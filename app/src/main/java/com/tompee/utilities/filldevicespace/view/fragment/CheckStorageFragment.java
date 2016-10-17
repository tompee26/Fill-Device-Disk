package com.tompee.utilities.filldevicespace.view.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.tompee.utilities.filldevicespace.R;
import com.tompee.utilities.filldevicespace.controller.storage.StorageUtility;
import com.tompee.utilities.filldevicespace.view.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class CheckStorageFragment extends Fragment implements View.OnClickListener {
    private TextView mFreeView;
    private TextView mFillView;
    private TextView mSystemView;
    private PieChart mChart;
    private View mSdCardView;
    private SharedPreferences mSharedPrefs;

    public static CheckStorageFragment getInstance() {
        return new CheckStorageFragment();
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
        View view = inflater.inflate(R.layout.fragment_check_storage, container, false);
        mFreeView = (TextView) view.findViewById(R.id.free_space);
        mFillView = (TextView) view.findViewById(R.id.fill_space);
        mSystemView = (TextView) view.findViewById(R.id.system);

        mChart = (PieChart) view.findViewById(R.id.chart);

        View refresh = view.findViewById(R.id.refresh);
        refresh.setOnClickListener(this);
        updateChart();
        updateView();

        mSdCardView = view.findViewById(R.id.sd_card);
        mSdCardView.setOnClickListener(this);
        setSdCardState();
        return view;
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

    private void updateChart() {
        mChart.setUsePercentValues(true);
        mChart.setDescription("");
        mChart.setExtraOffsets(5, 10, 5, 5);
        mChart.setDragDecelerationFrictionCoef(0.95f);
        mChart.setTouchEnabled(false);
        mChart.getLegend().setEnabled(false);

        mChart.setDrawHoleEnabled(true);
        mChart.setHoleColor(android.R.color.transparent);
        mChart.setDrawCenterText(false);

        mChart.setHoleRadius(95f);
        mChart.setTransparentCircleRadius(61f);
        mChart.setDrawCenterText(true);
        mChart.setRotationAngle(0);
        mChart.setDrawEntryLabels(false);
        mChart.setEntryLabelTextSize(12f);
        setData();
    }

    private void setData() {
        List<PieEntry> entries = new ArrayList<>();
        long free = StorageUtility.getAvailableStorageSize(getContext());
        long total = StorageUtility.getTotalStorageSize(getContext());
        long fill = StorageUtility.getFillSize(getContext());
        if (fill != 0) {
            entries.add(new PieEntry((float) fill / (float) total * 100,
                    getString(R.string.ids_legend_fill)));
        }
        if (free != 0) {
            entries.add(new PieEntry((float) free / (float) total * 100,
                    getString(R.string.ids_legend_free)));
        }
        entries.add(new PieEntry((float) (total - free - fill) / (float) total * 100,
                getString(R.string.ids_legend_system)));

        PieDataSet dataSet = new PieDataSet(entries, getString(R.string.ids_legend_storage));
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        List<Integer> colors = new ArrayList<>();
        colors.add(ContextCompat.getColor(getContext(), R.color.colorAccent));
        colors.add(ContextCompat.getColor(getContext(), R.color.colorPrimaryLight));
        colors.add(ContextCompat.getColor(getContext(), R.color.light_text_disable));
        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setDrawValues(false);
        mChart.setData(data);

        mChart.highlightValues(null);
        mChart.invalidate();
    }

    private void updateView() {
        if (mFreeView != null) {
            long free = StorageUtility.getAvailableStorageSize(getContext());
            mFreeView.setText(Formatter.formatFileSize(getContext(), free));

            long fill = StorageUtility.getFillSize(getContext());
            mFillView.setText(Formatter.formatFileSize(getContext(), fill));

            long total = StorageUtility.getTotalStorageSize(getContext());
            mSystemView.setText(Formatter.formatFileSize(getContext(), total - free - fill));
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        updateView();
        if (mChart != null) {
            updateChart();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.refresh:
                updateChart();
                updateView();
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
                updateView();
                break;
        }
    }
}

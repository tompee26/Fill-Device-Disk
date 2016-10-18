package com.tompee.utilities.filldevicespace.view.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
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
import com.tompee.utilities.filldevicespace.controller.storage.SdBroadcastReceiver;
import com.tompee.utilities.filldevicespace.controller.storage.StorageUtility;
import com.tompee.utilities.filldevicespace.view.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class CheckStorageFragment extends Fragment implements View.OnClickListener,
        SdBroadcastReceiver.StorageEventListener {
    private TextView mFreeView;
    private TextView mFillView;
    private TextView mSystemView;
    private PieChart mChart;
    private View mSdCardView;
    private SharedPreferences mSharedPrefs;
    private SdBroadcastReceiver mReceiver;

    public static CheckStorageFragment getInstance() {
        return new CheckStorageFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSharedPrefs = getContext().getSharedPreferences(MainActivity.
                SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        mReceiver = new SdBroadcastReceiver(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SdBroadcastReceiver.SD_CARD_ACTION);
        intentFilter.addAction(SdBroadcastReceiver.FILL_ACTION);
        intentFilter.addAction(SdBroadcastReceiver.CUSTOM_FILL_ACTION);
        intentFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        intentFilter.addAction(Intent.ACTION_MEDIA_EJECT);
        intentFilter.addDataScheme(SdBroadcastReceiver.STORAGE_INTENT_SCHEME);
        getActivity().registerReceiver(mReceiver, intentFilter);
        updateChart();
        updateView();
        setSdCardState();
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(mReceiver);
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

        mSdCardView = view.findViewById(R.id.sd_card);
        mSdCardView.setOnClickListener(this);
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
        if (fill != 0) {
            colors.add(ContextCompat.getColor(getContext(), R.color.colorAccent));
        }
        if (free != 0) {
            colors.add(ContextCompat.getColor(getContext(), R.color.colorPrimaryLight));
        }
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

            long systemSize = ((MainActivity)getActivity()).getSystemSize();
            mSystemView.setText(Formatter.formatFileSize(getContext(), systemSize));

            long totalSize = StorageUtility.getTotalStorageSize(getContext());
            mFillView.setText(Formatter.formatFileSize(getContext(), totalSize - systemSize - free));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.refresh:
                sendFillBroadcast(0.00f);
                break;
            case R.id.sd_card:
                SharedPreferences.Editor editor = mSharedPrefs.edit();
                if (mSharedPrefs.getBoolean(MainActivity.TAG_SD_CARD, false)) {
                    editor.putBoolean(MainActivity.TAG_SD_CARD, false);
                } else {
                    editor.putBoolean(MainActivity.TAG_SD_CARD, true);
                }
                editor.apply();
                Intent intent = new Intent(SdBroadcastReceiver.SD_CARD_ACTION, Uri.parse("file://"));
                getContext().sendBroadcast(intent);
                break;
        }
    }

    @Override
    public void onStorageChange() {
        setSdCardState();
        updateView();
        updateChart();
    }

    @Override
    public void onFill(float speed) {
        updateView();
        updateChart();
    }

    @Override
    public void onCustomFill(float speed, float percentage) {
        updateView();
        updateChart();
    }

    @Override
    public void onStorageStateChange() {
        setSdCardState();
    }

    private void sendFillBroadcast(float speed) {
        Intent intent = new Intent(SdBroadcastReceiver.FILL_ACTION, Uri.parse("file://"));
        intent.putExtra(SdBroadcastReceiver.EXTRA_SPEED, speed);
        getActivity().sendBroadcast(intent);
    }
}

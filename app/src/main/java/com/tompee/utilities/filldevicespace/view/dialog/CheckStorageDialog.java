package com.tompee.utilities.filldevicespace.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.tompee.utilities.filldevicespace.R;
import com.tompee.utilities.filldevicespace.controller.storage.StorageUtility;
import com.tompee.utilities.filldevicespace.view.SettingsActivity;

import java.util.ArrayList;
import java.util.List;

public class CheckStorageDialog extends BaseDialog implements DialogInterface.OnClickListener {
    private static final String TAG = "CheckStorageDialog";
    private PieChart mPieChart;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.dialog_check_storage, null);

        SharedPreferences sharedPreferences = getContext().getSharedPreferences(SettingsActivity.
                SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        if (sharedPreferences.getBoolean(SettingsActivity.TAG_CHECK_STORAGE_CHART, false)) {
            mPieChart = (PieChart) view.findViewById(R.id.chart);
            mPieChart.setUsePercentValues(true);
            mPieChart.setDescription("");
            mPieChart.setExtraOffsets(5, 10, 5, 5);
            mPieChart.setTouchEnabled(false);

            mPieChart.setDrawHoleEnabled(false);
            mPieChart.setTransparentCircleColor(ContextCompat.getColor(getContext(), R.color.light_text));
            mPieChart.setDrawCenterText(true);

            Legend l = mPieChart.getLegend();
            l.setPosition(Legend.LegendPosition.RIGHT_OF_CHART);
            l.setXEntrySpace(7f);
            l.setYEntrySpace(0f);
            l.setYOffset(0f);

            // entry label styling
            mPieChart.setEntryLabelColor(ContextCompat.getColor(getContext(), R.color.light_text));
            mPieChart.setEntryLabelTextSize(12f);
            setData();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.ids_title_available_space);
        builder.setMessage(String.format(getString(R.string.ids_message_check_storage),
                Formatter.formatShortFileSize(getContext(),
                        StorageUtility.getAvailableStorageSize(getContext()))));
        if (sharedPreferences.getBoolean(SettingsActivity.TAG_CHECK_STORAGE_CHART, false)) {
            builder.setView(view);
        }
        builder.setPositiveButton(R.string.ids_lbl_ok, this);
        return builder.create();
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
        colors.add(ContextCompat.getColor(getContext(), R.color.chart_primary));
        colors.add(ContextCompat.getColor(getContext(), R.color.chart_secondary));
        colors.add(ContextCompat.getColor(getContext(), R.color.chart_tertiary));
        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(ContextCompat.getColor(getContext(), R.color.light_text));
        mPieChart.setData(data);

        mPieChart.highlightValues(null);
        mPieChart.invalidate();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        Log.d(TAG, "" + which);
    }
}

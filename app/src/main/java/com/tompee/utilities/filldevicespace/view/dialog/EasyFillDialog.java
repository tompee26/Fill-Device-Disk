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
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.components.YAxis.AxisDependency;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.tompee.utilities.filldevicespace.R;
import com.tompee.utilities.filldevicespace.controller.task.FillDiskTask;
import com.tompee.utilities.filldevicespace.view.SettingsActivity;

public class EasyFillDialog extends BaseDialog implements FillDiskTask.FillDiskSpaceListener,
        DialogInterface.OnClickListener {
    private static final String TAG = "EasyFillDialog";
    private static final int MAX_VISIBLE_RANGE = 30;

    /* Set index */
    private static final int INDEX_SPEED = 0;
    private static final int INDEX_FILL_SIZE = 1;
    private static final int INDEX_FREE_SIZE = 2;

    /* Line chart constants */
    private static final float CIRCLE_RADIUS = 2.0f;
    private static final float LINE_WIDTH = 0.5f;
    private static final float VALUE_TEXT_SIZE = 4.0f;

    private FillDiskTask mFillDiskTask;
    private TextView mTotalTextView;
    private ProgressBar mTotalProgress;
    private TextView mTotalDataTextView;
    private LineChart mLineChartView;
    private float mPreviousSpeed;
    private boolean mIsChartEnabled;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(SettingsActivity.
                SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        mIsChartEnabled = sharedPreferences.getBoolean(SettingsActivity.TAG_FILL_CHART, false);

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.dialog_easy_fill, null);
        mTotalTextView = (TextView) view.findViewById(R.id.total_message);
        mTotalProgress = (ProgressBar) view.findViewById(R.id.total_progressbar);
        mTotalDataTextView = (TextView) view.findViewById(R.id.total_data);
        mLineChartView = (LineChart) view.findViewById(R.id.chart);
        if (mIsChartEnabled) {
            setChartProperties();
            setAxisProperties();
        } else {
            mLineChartView.setVisibility(View.GONE);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.ids_title_easy_fill);
        builder.setView(view);
        builder.setNegativeButton(R.string.ids_lbl_stop, this);
        builder.setNeutralButton(R.string.ids_lbl_pause, this);
        builder.setPositiveButton(R.string.ids_lbl_ok, this);
        startDiskTask();
        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        AlertDialog dialog = (AlertDialog) getDialog();
        Button button = dialog.getButton(Dialog.BUTTON_NEGATIVE);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelDiskTask();
                dismiss();
            }
        });
        button = dialog.getButton(Dialog.BUTTON_NEUTRAL);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFillDiskTask.isRunning()) {
                    mFillDiskTask.pause();
                    ((Button) v).setText(R.string.ids_lbl_resume);
                } else {
                    mFillDiskTask.resume();
                    ((Button) v).setText(R.string.ids_lbl_pause);
                }
            }
        });
        button = dialog.getButton(Dialog.BUTTON_POSITIVE);
        button.setEnabled(false);
    }

    private void setChartProperties() {
        mLineChartView.setTouchEnabled(false);
        mLineChartView.setDragEnabled(false);
        mLineChartView.setDescription("");
        mLineChartView.setDrawGridBackground(false);
        mLineChartView.setKeepPositionOnRotation(true);
        mLineChartView.setDragEnabled(true);
        mLineChartView.setData(new LineData());

        /* Speed Line Data Set */
        LineDataSet lineDataSet = new LineDataSet(null, getString(R.string.ids_legend_speed));
        lineDataSet.setLineWidth(LINE_WIDTH);
        lineDataSet.setColor(ContextCompat.getColor(getContext(), R.color.chart_primary));
        lineDataSet.setCircleRadius(CIRCLE_RADIUS);
        lineDataSet.setCircleColor(ContextCompat.getColor(getContext(), R.color.chart_primary));
        lineDataSet.setValueTextSize(VALUE_TEXT_SIZE);
        lineDataSet.setAxisDependency(AxisDependency.RIGHT);
        LineData data = mLineChartView.getData();
        data.addDataSet(lineDataSet);

        /* Fill Data Set */
        lineDataSet = new LineDataSet(null, getString(R.string.ids_legend_fill_size));
        lineDataSet.setLineWidth(LINE_WIDTH);
        lineDataSet.setColor(ContextCompat.getColor(getContext(), R.color.chart_secondary));
        lineDataSet.setCircleRadius(CIRCLE_RADIUS);
        lineDataSet.setCircleColor(ContextCompat.getColor(getContext(), R.color.chart_secondary));
        lineDataSet.setValueTextSize(VALUE_TEXT_SIZE);
        lineDataSet.setAxisDependency(AxisDependency.LEFT);
        lineDataSet.setDrawValues(true);
        data.addDataSet(lineDataSet);

        /* Free Line Data Set */
        lineDataSet = new LineDataSet(null, getString(R.string.ids_legend_free_size));
        lineDataSet.setLineWidth(LINE_WIDTH);
        lineDataSet.setColor(ContextCompat.getColor(getContext(), R.color.chart_tertiary));
        lineDataSet.setCircleRadius(CIRCLE_RADIUS);
        lineDataSet.setCircleColor(ContextCompat.getColor(getContext(), R.color.chart_tertiary));
        lineDataSet.setValueTextSize(VALUE_TEXT_SIZE);
        lineDataSet.setAxisDependency(AxisDependency.LEFT);
        lineDataSet.setDrawValues(true);
        data.addDataSet(lineDataSet);

        mLineChartView.invalidate();
    }

    private void setAxisProperties() {
        /* Customize X-Axis */
        XAxis xAxis = mLineChartView.getXAxis();
        xAxis.setTextSize(11f);
        xAxis.setTextColor(ContextCompat.getColor(getContext(), R.color.dark_text));
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawLabels(false);

        /* Customize Left-Axis */
        YAxis leftAxis = mLineChartView.getAxisLeft();
        leftAxis.setTextColor(ContextCompat.getColor(getContext(), R.color.dark_text));
        leftAxis.setAxisMinValue(0f);
        leftAxis.setDrawGridLines(true);
        leftAxis.setGranularityEnabled(true);
        leftAxis.setDrawLabels(false);

        /* Customize Right-Axis */
        YAxis rightAxis = mLineChartView.getAxisRight();
        rightAxis.setTextColor(ContextCompat.getColor(getContext(), R.color.dark_text));
        rightAxis.setAxisMinValue(0f);
        rightAxis.setDrawGridLines(false);
        rightAxis.setGranularityEnabled(false);
        rightAxis.setDrawLabels(false);
    }

    private void addEntry(int index, float data) {
        LineData lineData = mLineChartView.getData();
        lineData.addEntry(new Entry(lineData.getDataSetByIndex(0).getEntryCount(), data), index);
        lineData.notifyDataChanged();
    }

    private void startDiskTask() {
        if (mFillDiskTask == null) {
            mFillDiskTask = new FillDiskTask(getContext(), this);
            mFillDiskTask.execute(mIsChartEnabled);
        }
    }

    private void cancelDiskTask() {
        if (mFillDiskTask != null) {
            mFillDiskTask.cancel(true);
        }
    }

    @Override
    public void onFillDiskSpaceComplete() {
        AlertDialog dialog = (AlertDialog) getDialog();
        dialog.getButton(Dialog.BUTTON_POSITIVE).setEnabled(true);
        mTotalProgress.setProgress(100);
        mTotalDataTextView.setText(String.format(getString(R.string.
                ids_message_easy_fill_total_data), 100));
    }

    @Override
    public void onPreExecuteUpdate(long total) {
        mTotalTextView.setText(String.format(getString(R.string.ids_message_easy_fill_total),
                Formatter.formatFileSize(getContext(), total)));
    }

    @Override
    public void onProgressUpdate(int totalProgress, float speed, float fillSize, float free) {
        if (mIsChartEnabled) {
            if (mPreviousSpeed == 0) {
                mPreviousSpeed = speed;
            }
            float newSpeed = (mPreviousSpeed + speed) / 2;
            mPreviousSpeed = speed;
            addEntry(INDEX_SPEED, newSpeed);
            addEntry(INDEX_FILL_SIZE, fillSize);
            addEntry(INDEX_FREE_SIZE, free);
            mLineChartView.notifyDataSetChanged();
            mLineChartView.setVisibleXRangeMaximum(MAX_VISIBLE_RANGE);
            mLineChartView.moveViewTo(mLineChartView.getData().getEntryCount() - 7, 50f, AxisDependency.LEFT);
        }
        onProgressUpdate(totalProgress);
    }

    @Override
    public void onProgressUpdate(int totalProgress) {
        mTotalProgress.setProgress(totalProgress);
        mTotalDataTextView.setText(String.format(getString(R.string.
                ids_message_easy_fill_total_data), totalProgress));
    }

    @Override
    public void onCancelled() {
        mFillDiskTask = null;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        Log.d(TAG, "" + which);
    }
}

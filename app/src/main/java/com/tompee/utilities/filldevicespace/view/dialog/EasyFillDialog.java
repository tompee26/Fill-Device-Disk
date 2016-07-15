package com.tompee.utilities.filldevicespace.view.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class EasyFillDialog extends DialogFragment implements FillDiskTask.OnFillDiskSpaceListener {
    private static final int MAX_VISIBLE_RANGE = 30;

    /* Set index */
    private static final int INDEX_SPEED = 0;
    private static final int INDEX_FILL_SIZE = 1;
    private static final int INDEX_CURRENT_SIZE = 2;

    /* Line chart constants */
    private static final float CIRCLE_RADIUS = 2.0f;
    private static final float LINE_WIDTH = 0.5f;
    private static final float VALUE_TEXT_SIZE = 4.0f;

    private FillDiskTask mFillDiskTask;
    private TextView mTotalTextView;
    private ProgressBar mTotalProgress;
    private TextView mTotalDataTextView;
    private LineChart mLineChartView;

    public EasyFillDialog() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setCancelable(false);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().setCanceledOnTouchOutside(false);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.dialog_easy_fill, null);
        mTotalTextView = (TextView) view.findViewById(R.id.total_message);
        mTotalProgress = (ProgressBar) view.findViewById(R.id.total_progressbar);
        mTotalDataTextView = (TextView) view.findViewById(R.id.total_data);
        mLineChartView = (LineChart) view.findViewById(R.id.chart);
        setChartProperties();
        setAxisProperties();

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.ids_title_easy_fill);
        builder.setView(view);
        builder.setNegativeButton(R.string.ids_lbl_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cancelDiskTask();
                dismiss();
            }
        });
        startDiskTask();
        return builder.create();
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
        LineDataSet lineDataSet = new LineDataSet(null, "Speed (MB/sec)");
        lineDataSet.setLineWidth(LINE_WIDTH);
        lineDataSet.setColor(ContextCompat.getColor(getContext(), R.color.chart_speed));
        lineDataSet.setCircleRadius(CIRCLE_RADIUS);
        lineDataSet.setCircleColor(ContextCompat.getColor(getContext(), R.color.chart_speed));
        lineDataSet.setValueTextSize(VALUE_TEXT_SIZE);
        lineDataSet.setAxisDependency(AxisDependency.RIGHT);
        LineData data = mLineChartView.getData();
        data.addDataSet(lineDataSet);

        /* Fill Data Set */
        lineDataSet = new LineDataSet(null, "Fill (GB)");
        lineDataSet.setLineWidth(LINE_WIDTH);
        lineDataSet.setColor(ContextCompat.getColor(getContext(), R.color.chart_fill));
        lineDataSet.setCircleRadius(CIRCLE_RADIUS);
        lineDataSet.setCircleColor(ContextCompat.getColor(getContext(), R.color.chart_fill));
        lineDataSet.setValueTextSize(VALUE_TEXT_SIZE);
        lineDataSet.setAxisDependency(AxisDependency.LEFT);
        lineDataSet.setDrawValues(true);
        data.addDataSet(lineDataSet);

        /* Speed Line Data Set */
        lineDataSet = new LineDataSet(null, "Current (GB)");
        lineDataSet.setLineWidth(LINE_WIDTH);
        lineDataSet.setColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        lineDataSet.setCircleRadius(CIRCLE_RADIUS);
        lineDataSet.setCircleColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
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
            mFillDiskTask.execute();
        }
    }

    private void cancelDiskTask() {
        if (mFillDiskTask != null) {
            mFillDiskTask.cancel(true);
        }
    }

    @Override
    public void onFillDiskSpaceComplete() {
//        mProgressDialog.setMessage("Done!");
//        mProgressDialog.setProgress(100);
//        mProgressDialog.getButton(Dialog.BUTTON_POSITIVE).setEnabled(true);
    }

    @Override
    public void onPreExecuteUpdate(long total) {
        mTotalTextView.setText(String.format(getString(R.string.ids_message_easy_fill_total),
                Formatter.formatFileSize(getContext(), total)));
    }

    @Override
    public void onProgressUpdate(int totalProgress, float speed, float fillSize, float current) {
        addEntry(INDEX_SPEED, speed);
        addEntry(INDEX_FILL_SIZE, fillSize);
        addEntry(INDEX_CURRENT_SIZE, current);
        mLineChartView.notifyDataSetChanged();
        mLineChartView.setVisibleXRangeMaximum(MAX_VISIBLE_RANGE);
        mLineChartView.moveViewTo(mLineChartView.getData().getEntryCount() - 7, 50f, AxisDependency.LEFT);

        mTotalProgress.setProgress(totalProgress);
        mTotalDataTextView.setText(String.format(getString(R.string.
                ids_message_easy_fill_total_data), totalProgress));
    }

    @Override
    public void onCancelled() {
        mFillDiskTask = null;
    }
}

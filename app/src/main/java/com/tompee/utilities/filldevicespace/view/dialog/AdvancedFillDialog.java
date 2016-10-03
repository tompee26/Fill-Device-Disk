package com.tompee.utilities.filldevicespace.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.NativeExpressAdView;
import com.tompee.utilities.filldevicespace.BuildConfig;
import com.tompee.utilities.filldevicespace.R;
import com.tompee.utilities.filldevicespace.controller.PauseableHandler;
import com.tompee.utilities.filldevicespace.controller.Utilities;
import com.tompee.utilities.filldevicespace.controller.Utilities.Unit;
import com.tompee.utilities.filldevicespace.controller.storage.StorageUtility;
import com.tompee.utilities.filldevicespace.controller.task.FillDiskTask;
import com.tompee.utilities.filldevicespace.view.SettingsActivity;

public class AdvancedFillDialog extends BaseDialog implements View.OnClickListener, TextWatcher,
        FillDiskTask.FillDiskTaskListener, AdapterView.OnItemSelectedListener,
        PauseableHandler.PauseableHandlerCallback {
    private static final int FINISH_MESSAGE = 1;

    /* Set index */
    private static final int INDEX_SPEED = 0;
    private static final int INDEX_FILL_SIZE = 1;
    private static final int INDEX_FREE_SIZE = 2;

    /* Line chart constants */
    private static final float CIRCLE_RADIUS = 2.0f;
    private static final float LINE_WIDTH = 0.5f;
    private static final float VALUE_TEXT_SIZE = 4.0f;

    private Button mPositiveButton;
    private Button mNegativeButton;
    private Button mNeutralButton;
    private TextView mFreeTextView;
    private Spinner mSpinner;
    private EditText mEditText;
    private ProgressBar mProgress;
    private TextView mDataTextView;
    private LineChart mLineChartView;

    private FillDiskTask mFillDiskTask;
    private PauseableHandler mPauseableHandler;
    private boolean mIsChartEnabled;
    private boolean mIsFinished;
    private float mPreviousSpeed;
    private int mMaxRange;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(SettingsActivity.
                SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        mIsChartEnabled = sharedPreferences.getBoolean(SettingsActivity.TAG_FILL_CHART, false);
        mMaxRange = sharedPreferences.getInt(SettingsActivity.TAG_MAX_VISIBLE_RANGE,
                SettingsActivity.DEFAULT_VISIBLE_RANGE);
        mPauseableHandler = new PauseableHandler(this);

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.dialog_advanced_fill, null);
        mFreeTextView = (TextView) view.findViewById(R.id.total_message);
        setFreeSpaceText();

        mSpinner = (Spinner) view.findViewById(R.id.unit);
        mSpinner.setOnItemSelectedListener(this);

        ArrayAdapter<Unit> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, Unit.values());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);

        mEditText = (EditText) view.findViewById(R.id.value);
        mEditText.addTextChangedListener(this);

        mProgress = (ProgressBar) view.findViewById(R.id.progressbar);
        mDataTextView = (TextView) view.findViewById(R.id.progress_data);

        mLineChartView = (LineChart) view.findViewById(R.id.chart);
        if (mIsChartEnabled) {
            setChartProperties();
            setAxisProperties();
        } else {
            mLineChartView.setVisibility(View.GONE);
        }

        NativeExpressAdView adView = (NativeExpressAdView) view.findViewById(R.id.adView);
        AdRequest.Builder build = new AdRequest.Builder();
        if (BuildConfig.DEBUG) {
            build.addTestDevice("3AD737A018BB67E7108FD1836E34DD1C");
        }
        adView.loadAd(build.build());

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.ids_title_advance_fill);
        builder.setView(view);
        builder.setNegativeButton(R.string.ids_lbl_cancel, this);
        builder.setNeutralButton(R.string.ids_lbl_pause, this);
        builder.setPositiveButton(R.string.ids_lbl_start, this);
        return builder.create();
    }

    @Override
    public void onResume() {
        super.onResume();
        mPauseableHandler.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mPauseableHandler.pause();
        if (mFillDiskTask != null && mFillDiskTask.isRunning()) {
            mFillDiskTask.pause();
            AlertDialog dialog = (AlertDialog) getDialog();
            if (dialog != null) {
                Button button = dialog.getButton(Dialog.BUTTON_NEUTRAL);
                button.setText(R.string.ids_lbl_resume);
            }
        }
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
        lineDataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
        LineData data = mLineChartView.getData();
        data.addDataSet(lineDataSet);

        /* Fill Data Set */
        lineDataSet = new LineDataSet(null, getString(R.string.ids_legend_fill_size));
        lineDataSet.setLineWidth(LINE_WIDTH);
        lineDataSet.setColor(ContextCompat.getColor(getContext(), R.color.chart_secondary));
        lineDataSet.setCircleRadius(CIRCLE_RADIUS);
        lineDataSet.setCircleColor(ContextCompat.getColor(getContext(), R.color.chart_secondary));
        lineDataSet.setValueTextSize(VALUE_TEXT_SIZE);
        lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        lineDataSet.setDrawValues(true);
        data.addDataSet(lineDataSet);

        /* Free Line Data Set */
        lineDataSet = new LineDataSet(null, getString(R.string.ids_legend_free_size));
        lineDataSet.setLineWidth(LINE_WIDTH);
        lineDataSet.setColor(ContextCompat.getColor(getContext(), R.color.chart_tertiary));
        lineDataSet.setCircleRadius(CIRCLE_RADIUS);
        lineDataSet.setCircleColor(ContextCompat.getColor(getContext(), R.color.chart_tertiary));
        lineDataSet.setValueTextSize(VALUE_TEXT_SIZE);
        lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
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

    @Override
    public void onStart() {
        super.onStart();
        AlertDialog dialog = (AlertDialog) getDialog();
        mNeutralButton = dialog.getButton(DialogInterface.BUTTON_NEUTRAL);
        mNeutralButton.setVisibility(View.INVISIBLE);
        mNeutralButton.setOnClickListener(this);

        mNegativeButton = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        mNegativeButton.setOnClickListener(this);

        mPositiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        mPositiveButton.setOnClickListener(this);
        mPositiveButton.setEnabled(false);
        mPositiveButton.setOnClickListener(this);
    }

    private void setFreeSpaceText() {
        mFreeTextView.setText(String.format(getString(R.string.ids_lbl_check_storage_size),
                Formatter.formatFileSize(getContext(),
                        StorageUtility.getAvailableStorageSize(getContext()))));
    }

    private void startAdvanceFill() {
        mNegativeButton.setText(R.string.ids_lbl_stop);
        mPositiveButton.setText(R.string.ids_lbl_ok);
        mPositiveButton.setEnabled(false);
        mEditText.setEnabled(false);
        mSpinner.setEnabled(false);
        mProgress.setVisibility(View.VISIBLE);
        mDataTextView.setVisibility(View.VISIBLE);
        mNeutralButton.setVisibility(View.VISIBLE);
        if (mIsChartEnabled) {
            mLineChartView.setVisibility(View.VISIBLE);
        }
        if (mFillDiskTask == null) {
            mFillDiskTask = new FillDiskTask(getContext(), this, Utilities.convertToBytes(Integer.
                            parseInt(mEditText.getText().toString()),
                    Unit.valueOf(mSpinner.getSelectedItem().toString())));
            mFillDiskTask.execute(mIsChartEnabled);
        }
    }

    private void cancelDiskTask() {
        if (mFillDiskTask != null) {
            mFillDiskTask.cancel(true);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.equals(mPositiveButton)) {
            if (mIsFinished) {
                dismiss();
            } else {
                startAdvanceFill();
            }
        } else if (v.equals(mNegativeButton)) {
            cancelDiskTask();
            dismiss();
        } else {
            if (mFillDiskTask.isRunning()) {
                mFillDiskTask.pause();
                ((Button) v).setText(R.string.ids_lbl_resume);
            } else {
                mFillDiskTask.resume();
                ((Button) v).setText(R.string.ids_lbl_pause);
            }
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        try {
            long free = StorageUtility.getAvailableStorageSize(getContext());
            long input = Utilities.convertToBytes(Integer.parseInt(mEditText.getText().toString()),
                    Unit.valueOf(mSpinner.getSelectedItem().toString()));
            if (input != 0 && input <= free) {
                mPositiveButton.setEnabled(true);
            } else {
                mPositiveButton.setEnabled(false);
            }
        } catch (NumberFormatException e) {
            mPositiveButton.setEnabled(false);
        }
    }

    @Override
    public void onFillDiskSpaceComplete() {
        Message newMessage = Message.obtain(mPauseableHandler, FINISH_MESSAGE);
        mPauseableHandler.sendMessage(newMessage);
    }

    @Override
    public void onPreExecuteUpdate(long total) {
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
            mLineChartView.setVisibleXRangeMaximum(mMaxRange);
            mLineChartView.moveViewTo(mLineChartView.getData().getEntryCount() - 7, 50f, YAxis.AxisDependency.LEFT);
        }

        onProgressUpdate(totalProgress);
    }

    @Override
    public void onProgressUpdate(int totalProgress) {
        setFreeSpaceText();
        mProgress.setProgress(totalProgress);
        mDataTextView.setText(String.format(getString(R.string.
                ids_lbl_easy_fill_total_data), totalProgress));
    }

    @Override
    public void onCancelled() {
        mFillDiskTask = null;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        afterTextChanged(null);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public boolean storeMessage(Message message) {
        return message.what == FINISH_MESSAGE;
    }

    @Override
    public void processMessage(Message message) {
        mIsFinished = true;
        mPositiveButton.setEnabled(true);
        mNeutralButton.setVisibility(View.INVISIBLE);
        mNegativeButton.setVisibility(View.INVISIBLE);
        mProgress.setProgress(100);
        mDataTextView.setText(String.format(getString(R.string.
                ids_lbl_easy_fill_total_data), 100));
    }
}

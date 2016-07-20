package com.tompee.utilities.filldevicespace.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.tompee.utilities.filldevicespace.R;
import com.tompee.utilities.filldevicespace.controller.Utilities;
import com.tompee.utilities.filldevicespace.controller.Utilities.Unit;
import com.tompee.utilities.filldevicespace.controller.storage.StorageUtility;
import com.tompee.utilities.filldevicespace.controller.task.FillDiskTask;
import com.tompee.utilities.filldevicespace.view.SettingsActivity;

public class AdvancedFillDialog extends BaseDialog implements DialogInterface.OnClickListener,
        View.OnClickListener, TextWatcher, FillDiskTask.FillDiskTaskListener {
    private static final String TAG = "AdvancedFillDialog";

    private Button mPositiveButton;
    private Button mNegativeButton;
    private Button mNeutralButton;
    private TextView mFreeTextView;
    private Spinner mSpinner;
    private EditText mEditText;
    private ProgressBar mProgress;
    private TextView mDataTextView;

    private FillDiskTask mFillDiskTask;
    private boolean mIsChartEnabled;
    private boolean mIsFinished;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(SettingsActivity.
                SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        mIsChartEnabled = sharedPreferences.getBoolean(SettingsActivity.TAG_FILL_CHART, false);

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.dialog_advanced_fill, null);
        mFreeTextView = (TextView) view.findViewById(R.id.total_message);
        setFreeSpaceText();

        mSpinner = (Spinner) view.findViewById(R.id.unit);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.ids_unit_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);

        mEditText = (EditText) view.findViewById(R.id.value);
        mEditText.addTextChangedListener(this);

        mProgress = (ProgressBar) view.findViewById(R.id.progressbar);
        mDataTextView = (TextView) view.findViewById(R.id.progress_data);

//        mTotalProgress = (ProgressBar) view.findViewById(R.id.total_progressbar);
//        mTotalDataTextView = (TextView) view.findViewById(R.id.total_data);
//        mLineChartView = (LineChart) view.findViewById(R.id.chart);
//        if (mIsChartEnabled) {
//            setChartProperties();
//            setAxisProperties();
//        } else {
//            mLineChartView.setVisibility(View.GONE);
//        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.ids_title_advance_fill);
        builder.setView(view);
        builder.setNegativeButton(R.string.ids_lbl_cancel, this);
        builder.setNeutralButton(R.string.ids_lbl_pause, this);
        builder.setPositiveButton(R.string.ids_lbl_start, this);
//        startDiskTask();
        return builder.create();
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
        mFreeTextView.setText(String.format(getString(R.string.ids_message_check_storage),
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
    public void onClick(DialogInterface dialog, int which) {
        Log.d(TAG, "" + which);
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
            if (input <= free) {
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
        mIsFinished = true;
        mPositiveButton.setEnabled(true);
        mNeutralButton.setVisibility(View.INVISIBLE);
        mNegativeButton.setVisibility(View.INVISIBLE);
        mProgress.setProgress(100);
        mDataTextView.setText(String.format(getString(R.string.
                ids_message_easy_fill_total_data), 100));
    }

    @Override
    public void onPreExecuteUpdate(long total) {
    }

    @Override
    public void onProgressUpdate(int totalProgress, float speed, float fillSize, float free) {
        onProgressUpdate(totalProgress);
    }

    @Override
    public void onProgressUpdate(int totalProgress) {
        setFreeSpaceText();
        mProgress.setProgress(totalProgress);
        mDataTextView.setText(String.format(getString(R.string.
                ids_message_easy_fill_total_data), totalProgress));
    }

    @Override
    public void onCancelled() {
        mFillDiskTask = null;
    }
}

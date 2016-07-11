package com.tompee.utilities.filldevicespace.view;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.tompee.utilities.filldevicespace.R;
import com.tompee.utilities.filldevicespace.controller.storage.StorageUtility;
import com.tompee.utilities.filldevicespace.controller.task.FillDiskTask;
import com.tompee.utilities.filldevicespace.view.base.BaseActivity;

public class MainActivity extends BaseActivity implements View.OnClickListener,
        FillDiskTask.OnFillDiskSpaceListener {
    private static final String TAG = "MainActivity";
    private ProgressDialog mProgressDialog;
    private FillDiskTask mFillDiskTask;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top);
        setToolbar(R.id.toolbar, false);
        TextView title = (TextView) findViewById(R.id.toolbar_text);
        title.setText(R.string.app_name);

        View view = findViewById(R.id.easy_fill);
        view.setOnClickListener(this);
        view = findViewById(R.id.delete);
        view.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.easy_fill:
                showProgressDialog();
                startDiskTask();
                break;
            case R.id.delete:
                StorageUtility.deleteFiles(this);
        }
    }

    private void showProgressDialog() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setTitle(R.string.ids_title_filling);
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setMessage(getString(R.string.ids_message_calculating));
        mProgressDialog.setCancelable(false);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setProgressNumberFormat(null);
        mProgressDialog.setButton(Dialog.BUTTON_NEGATIVE, getString(R.string.ids_lbl_cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cancelDiskFillTask();
                        dialog.dismiss();
                    }
                });
        mProgressDialog.show();
    }

    private void cancelDiskFillTask() {
        if (mProgressDialog != null) {
            mProgressDialog = null;
        }
        if (mFillDiskTask != null) {
            mFillDiskTask.cancel(true);
        }
    }

    private void startDiskTask() {
        if (mFillDiskTask == null) {
            mFillDiskTask = new FillDiskTask(this, this);
            mFillDiskTask.execute();
        }
    }

    @Override
    public void onFillDiskSpaceComplete() {
        mProgressDialog.dismiss();
        mProgressDialog = null;
        mFillDiskTask = null;
    }

    @Override
    public void onProgressUpdate(long current, int progress) {
        if (mProgressDialog != null) {
            mProgressDialog.setMessage(String.format(getString(R.string.ids_message_free_space),
                    Formatter.formatShortFileSize(this, current)));
            mProgressDialog.setProgress(progress);
        }
    }

    @Override
    public void onCancelled() {
        Log.d(TAG, "Fill cancelled");
        mFillDiskTask = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelDiskFillTask();
    }
}

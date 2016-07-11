package com.tompee.utilities.filldevicespace.view;

import android.app.AlertDialog;
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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.easy_fill:
                showProgressDialog();
                startDiskTask();
                break;
            case R.id.advance_fill:
                break;
            case R.id.check_storage:
                showCheckStorageDialog();
                break;
            case R.id.delete:
                showClearFillDialog();
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
        mProgressDialog.setButton(Dialog.BUTTON_POSITIVE, getString(R.string.ids_lbl_ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mProgressDialog.dismiss();
                        mProgressDialog = null;
                        mFillDiskTask = null;
                    }
                });
        mProgressDialog.show();
        mProgressDialog.getButton(Dialog.BUTTON_POSITIVE).setEnabled(false);
    }

    private void showCheckStorageDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.ids_title_available_space));
        builder.setMessage(String.format(getString(R.string.ids_message_free_space),
                Formatter.formatShortFileSize(this, StorageUtility.getAvailableStorageSize(this))));
        builder.setPositiveButton(R.string.ids_lbl_ok, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showClearFillDialog() {
        long before = StorageUtility.getAvailableStorageSize(this);
        StorageUtility.deleteFiles(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.ids_lbl_clear));
        builder.setMessage(String.format(getString(R.string.ids_message_cleared_space),
                Formatter.formatShortFileSize(this, StorageUtility.
                        getAvailableStorageSize(this) - before)));
        builder.setPositiveButton(R.string.ids_lbl_ok, null);
        AlertDialog dialog = builder.create();
        dialog.show();
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
        mProgressDialog.setMessage("Done!");
        mProgressDialog.setProgress(100);
        mProgressDialog.getButton(Dialog.BUTTON_POSITIVE).setEnabled(true);
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

package com.tompee.utilities.filldevicespace.view;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.text.format.Formatter;
import android.view.View;
import android.widget.TextView;

import com.tompee.utilities.filldevicespace.R;
import com.tompee.utilities.filldevicespace.controller.storage.StorageUtility;
import com.tompee.utilities.filldevicespace.view.base.BaseActivity;
import com.tompee.utilities.filldevicespace.view.dialog.EasyFillDialog;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private static final String DIALOG_EASY_FILL = "dialog_easy_fill";

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
                showEasyFillDialog();
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

    private void showEasyFillDialog() {
        final FragmentManager fragmentManager = getSupportFragmentManager();
        EasyFillDialog dialog = new EasyFillDialog();
        dialog.show(fragmentManager, DIALOG_EASY_FILL);
    }

    private void showCheckStorageDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.ids_title_available_space));
        builder.setMessage(String.format(getString(R.string.ids_message_check_storage),
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
}

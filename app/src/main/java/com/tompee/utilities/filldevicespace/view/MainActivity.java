package com.tompee.utilities.filldevicespace.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.TextView;

import com.tompee.utilities.filldevicespace.R;
import com.tompee.utilities.filldevicespace.view.base.BaseActivity;
import com.tompee.utilities.filldevicespace.view.dialog.CheckStorageDialog;
import com.tompee.utilities.filldevicespace.view.dialog.ClearFillDialog;
import com.tompee.utilities.filldevicespace.view.dialog.EasyFillDialog;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private static final String DIALOG_EASY_FILL = "dialog_easy_fill";
    private static final String DIALOG_CHECK_SPACE = "dialog_check_space";
    private static final String DIALOG_CLEAR_FILL = "dialog_clear_fill";

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
        Intent intent;
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
                break;
            case R.id.settings:
                intent = new Intent(this, SettingsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            case R.id.help:
                intent = new Intent(this, HelpActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
        }
    }

    private void showEasyFillDialog() {
        final FragmentManager fragmentManager = getSupportFragmentManager();
        EasyFillDialog dialog = new EasyFillDialog();
        dialog.show(fragmentManager, DIALOG_EASY_FILL);
    }

    private void showCheckStorageDialog() {
        final FragmentManager fragmentManager = getSupportFragmentManager();
        CheckStorageDialog dialog = new CheckStorageDialog();
        dialog.show(fragmentManager, DIALOG_CHECK_SPACE);
    }

    private void showClearFillDialog() {
        final FragmentManager fragmentManager = getSupportFragmentManager();
        ClearFillDialog dialog = new ClearFillDialog();
        dialog.show(fragmentManager, DIALOG_CLEAR_FILL);
    }
}

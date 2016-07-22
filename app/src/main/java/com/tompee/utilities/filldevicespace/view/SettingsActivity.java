package com.tompee.utilities.filldevicespace.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import com.tompee.utilities.filldevicespace.R;
import com.tompee.utilities.filldevicespace.controller.storage.StorageUtility;
import com.tompee.utilities.filldevicespace.view.base.BaseActivity;
import com.tompee.utilities.filldevicespace.view.dialog.SetRangeDialog;

public class SettingsActivity extends BaseActivity implements View.OnClickListener,
        SetRangeDialog.OnSetRangeListener {
    public static final String SHARED_PREFERENCE_NAME = "fill_device_disk_shared_prefs";
    public static final String TAG_SD_CARD = "sd_card";
    public static final String TAG_FILL_CHART = "fill_chart";
    public static final String TAG_MAX_VISIBLE_RANGE = "max_visible_range";
    public static final String TAG_CHECK_STORAGE_CHART = "check_storage_chart";
    public static final int DEFAULT_VISIBLE_RANGE = 30;
    private static final String DIALOG_RANGE = "dialog_easy_fill";
    private SharedPreferences mSharedPrefs;
    private TextView mVisibleRangeTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setToolbar(R.id.toolbar, true);
        TextView title = (TextView) findViewById(R.id.toolbar_text);
        title.setText(R.string.ids_title_settings);

        mSharedPrefs = getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        mVisibleRangeTextView = (TextView) findViewById(R.id.tv_visible_range);
        int points = mSharedPrefs.getInt(TAG_MAX_VISIBLE_RANGE, DEFAULT_VISIBLE_RANGE);
        mVisibleRangeTextView.setText(getResources().
                getQuantityString(R.plurals.ids_lbl_range_points, points, points));

        Switch sw = (Switch) findViewById(R.id.switch_sd_card);
        if (StorageUtility.getRemovableStorage(this) == null) {
            sw.setEnabled(false);
            sw.setChecked(false);
        } else {
            sw.setEnabled(true);
            sw.setChecked(mSharedPrefs.getBoolean(TAG_SD_CARD, false));
        }
        sw = (Switch) findViewById(R.id.switch_fill_chart);
        sw.setChecked(mSharedPrefs.getBoolean(TAG_FILL_CHART, false));
        sw = (Switch) findViewById(R.id.switch_check_storage_chart);
        sw.setChecked(mSharedPrefs.getBoolean(TAG_CHECK_STORAGE_CHART, false));
    }

    @Override
    public void onClick(View v) {
        Switch sw;
        SharedPreferences.Editor editor = mSharedPrefs.edit();
        boolean isChecked;
        switch (v.getId()) {
            case R.id.sd_card:
                if (StorageUtility.getRemovableStorage(this) != null) {
                    sw = (Switch) findViewById(R.id.switch_sd_card);
                    isChecked = !sw.isChecked();
                    sw.setChecked(isChecked);
                    editor.putBoolean(TAG_SD_CARD, isChecked);
                }
                break;
            case R.id.chart_fill:
                sw = (Switch) findViewById(R.id.switch_fill_chart);
                isChecked = !sw.isChecked();
                sw.setChecked(isChecked);
                editor.putBoolean(TAG_FILL_CHART, isChecked);
                break;
            case R.id.chart_check_storage:
                sw = (Switch) findViewById(R.id.switch_check_storage_chart);
                isChecked = !sw.isChecked();
                sw.setChecked(isChecked);
                editor.putBoolean(TAG_CHECK_STORAGE_CHART, isChecked);
                break;
            case R.id.chart_max_range:
                FragmentManager fragmentManager = getSupportFragmentManager();
                if (fragmentManager.findFragmentByTag(DIALOG_RANGE) == null) {
                    SetRangeDialog dialog = new SetRangeDialog();
                    dialog.show(fragmentManager, DIALOG_RANGE);
                }
                break;
        }
        editor.apply();
    }

    @Override
    public void onValueChanged(int value) {
        SharedPreferences.Editor editor = mSharedPrefs.edit();
        editor.putInt(TAG_MAX_VISIBLE_RANGE, value);
        editor.apply();
        mVisibleRangeTextView.setText(getResources().
                getQuantityString(R.plurals.ids_lbl_range_points, value, value));
    }
}

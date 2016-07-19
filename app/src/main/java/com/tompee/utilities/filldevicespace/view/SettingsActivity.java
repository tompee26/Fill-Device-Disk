package com.tompee.utilities.filldevicespace.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import com.tompee.utilities.filldevicespace.R;
import com.tompee.utilities.filldevicespace.view.base.BaseActivity;

public class SettingsActivity extends BaseActivity implements View.OnClickListener {
    public static final String SHARED_PREFERENCE_NAME = "fill_device_disk_shared_prefs";
    public static final String TAG_SD_CARD = "sd_card";
    public static final String TAG_FILL_CHART = "fill_chart";
    public static final String TAG_CHECK_STORAGE_CHART = "check_storage_chart";
    private SharedPreferences mSharedPrefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setToolbar(R.id.toolbar, true);
        TextView title = (TextView) findViewById(R.id.toolbar_text);
        title.setText(R.string.ids_title_settings);

        mSharedPrefs = getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);

        Switch sw = (Switch) findViewById(R.id.switch_sd_card);
        sw.setChecked(mSharedPrefs.getBoolean(TAG_SD_CARD, false));
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
                sw = (Switch) findViewById(R.id.switch_sd_card);
                isChecked = !sw.isChecked();
                sw.setChecked(isChecked);
                editor.putBoolean(TAG_SD_CARD, isChecked);
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
        }
        editor.apply();
    }
}

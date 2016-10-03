package com.tompee.utilities.filldevicespace.view;

import android.os.Bundle;
import android.widget.TextView;

import com.tompee.utilities.filldevicespace.BuildConfig;
import com.tompee.utilities.filldevicespace.R;
import com.tompee.utilities.filldevicespace.view.base.BaseActivity;

public class AboutActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setToolbar(true);
        TextView title = (TextView) findViewById(R.id.toolbar_text);
        title.setText(R.string.ids_lbl_about);

        TextView version = (TextView) findViewById(R.id.version);
        version.setText(String.format(getString(R.string.ids_message_version), BuildConfig.VERSION_NAME));
    }
}

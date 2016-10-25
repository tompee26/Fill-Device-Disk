package com.tompee.utilities.filldevicespace.view;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.ImageView;
import android.widget.TextView;

import com.tompee.utilities.filldevicespace.BuildConfig;
import com.tompee.utilities.filldevicespace.R;
import com.tompee.utilities.filldevicespace.controller.Utilities;
import com.tompee.utilities.filldevicespace.view.base.BaseActivity;

public class HelpActivity extends BaseActivity {
    public static final String TAG_MODE = "mode";
    public static final int ABOUT = 0;
    public static final int LICENSE = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        int mode = getIntent().getIntExtra(TAG_MODE, ABOUT);
        if (mode == ABOUT) {
            setContentView(R.layout.activity_about);
            setToolbar(true);
            TextView title = (TextView) findViewById(R.id.toolbar_text);
            title.setText(R.string.ids_lbl_about);

            TextView version = (TextView) findViewById(R.id.version);
            version.setText(String.format(getString(R.string.ids_message_version), BuildConfig.VERSION_NAME));
        } else if (mode == LICENSE) {
            setContentView(R.layout.activity_license);
            setToolbar(true);

            TextView title = (TextView) findViewById(R.id.toolbar_text);
            title.setText(R.string.ids_title_open_source);

            TextView content = (TextView) findViewById(R.id.content);
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                content.setText(Html.fromHtml(Utilities.getStringFromAsset(this, "opensource.html"),
                        Html.FROM_HTML_MODE_LEGACY));
            } else {
                content.setText(Html.fromHtml(Utilities.getStringFromAsset(this, "opensource.html")));
            }
            content.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }
}

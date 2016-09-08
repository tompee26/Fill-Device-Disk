package com.tompee.utilities.filldevicespace.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;

import com.tompee.utilities.filldevicespace.BuildConfig;
import com.tompee.utilities.filldevicespace.R;
import com.tompee.utilities.filldevicespace.view.base.BaseActivity;

public class HelpActivity extends BaseActivity implements View.OnClickListener {
    private static final String LICENSE_URL = "file:///android_asset/opensource.html";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        setToolbar(R.id.toolbar, true);
        TextView title = (TextView) findViewById(R.id.toolbar_text);
        title.setText(R.string.ids_title_help);

        TextView tvAbout = (TextView) findViewById(R.id.textview_about);
        tvAbout.setText(String.format(getString(R.string.ids_lbl_about),
                getString(R.string.app_name)));
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        AlertDialog.Builder builder;
        switch (v.getId()) {
            case R.id.about:
                builder = new AlertDialog.Builder(this);
                builder.setTitle(getString(R.string.app_name) + " " + BuildConfig.VERSION_NAME);
                builder.setMessage(R.string.ids_message_copyright);
                builder.setPositiveButton(R.string.ids_lbl_ok, null);
                builder.create().show();
                break;
            case R.id.contact:
                builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.ids_lbl_contact);
                builder.setMessage(R.string.ids_message_contact);
                builder.setPositiveButton(R.string.ids_lbl_ok, null);
                builder.create().show();
                break;
            case R.id.license:
                intent = new Intent(this, WebViewActivity.class);
                intent.setData(Uri.parse(LICENSE_URL));
                intent.putExtra(WebViewActivity.TAG_TITLE, getString(R.string.ids_title_open_source));
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
        }
    }
}

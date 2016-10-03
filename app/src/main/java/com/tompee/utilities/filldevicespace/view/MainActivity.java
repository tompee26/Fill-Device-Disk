package com.tompee.utilities.filldevicespace.view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.tompee.utilities.filldevicespace.BuildConfig;
import com.tompee.utilities.filldevicespace.R;
import com.tompee.utilities.filldevicespace.view.base.BaseActivity;
import com.tompee.utilities.filldevicespace.view.dialog.AdvancedFillDialog;
import com.tompee.utilities.filldevicespace.view.dialog.CheckStorageDialog;
import com.tompee.utilities.filldevicespace.view.dialog.ClearFillDialog;
import com.tompee.utilities.filldevicespace.view.dialog.EasyFillDialog;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private static final String DIALOG_EASY_FILL = "dialog_easy_fill";
    private static final String DIALOG_CHECK_SPACE = "dialog_check_space";
    private static final String DIALOG_CLEAR_FILL = "dialog_clear_fill";
    private static final String DIALOG_ADVANCED_FILL = "dialog_advanced_fill";
    private static final String LICENSE_URL = "file:///android_asset/opensource.html";

    private static final String SHARED_PREF = "filldevicedisksharedpref";
    private static final String LAUNCH_COUNT = "launch_count";
    private static final int MIN_LAUNCH_COUNT = 4;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top);
        setToolbar(false);
        TextView title = (TextView) findViewById(R.id.toolbar_text);
        title.setText(R.string.app_name);

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        int launchCount = sharedPreferences.getInt(LAUNCH_COUNT, 0);
        if (launchCount == MIN_LAUNCH_COUNT) {
            editor.putInt(LAUNCH_COUNT, 0);
            showAppRater();
        } else {
            editor.putInt(LAUNCH_COUNT, ++launchCount);
        }
        editor.apply();

        AdView adView = (AdView) findViewById(R.id.adView);
        AdRequest.Builder builder = new AdRequest.Builder();
        if (BuildConfig.DEBUG) {
            builder.addTestDevice("3AD737A018BB67E7108FD1836E34DD1C");
        }
        adView.loadAd(builder.build());
    }

    private void showAppRater() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.ids_title_rate);
        builder.setMessage(R.string.ids_message_rate);
        builder.setNeutralButton(R.string.ids_lbl_remind, null);
        builder.setNegativeButton(R.string.ids_lbl_no_rate, null);
        builder.setPositiveButton(R.string.ids_lbl_yes_rate, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" +
                        BuildConfig.APPLICATION_ID));
                startActivity(intent);
            }
        });
        builder.create().show();
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.easy_fill:
                showEasyFillDialog();
                break;
            case R.id.advance_fill:
                showAdvancedFillDialog();
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
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.menu_about:
                intent = new Intent(this, AboutActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            case R.id.menu_contact:
                intent = new Intent(Intent.ACTION_SEND);
                intent.setType("message/rfc822");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"tompee26@gmail.com"});
                intent.putExtra(Intent.EXTRA_SUBJECT, "Re: Fill Device Disk");
                intent.putExtra(Intent.EXTRA_TEXT, "");
                startActivity(Intent.createChooser(intent, "Send mail..."));
                return true;
            case R.id.menu_os:
                intent = new Intent(this, WebViewActivity.class);
                intent.setData(Uri.parse(LICENSE_URL));
                intent.putExtra(WebViewActivity.TAG_TITLE, getString(R.string.ids_title_open_source));
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showEasyFillDialog() {
        final FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.findFragmentByTag(DIALOG_EASY_FILL) == null) {
            EasyFillDialog dialog = new EasyFillDialog();
            dialog.show(fragmentManager, DIALOG_EASY_FILL);
        }
    }

    private void showCheckStorageDialog() {
        final FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.findFragmentByTag(DIALOG_CHECK_SPACE) == null) {
            CheckStorageDialog dialog = new CheckStorageDialog();
            dialog.show(fragmentManager, DIALOG_CHECK_SPACE);
        }
    }

    private void showClearFillDialog() {
        final FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.findFragmentByTag(DIALOG_CLEAR_FILL) == null) {
            ClearFillDialog dialog = new ClearFillDialog();
            dialog.show(fragmentManager, DIALOG_CLEAR_FILL);
        }
    }

    private void showAdvancedFillDialog() {
        final FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.findFragmentByTag(DIALOG_ADVANCED_FILL) == null) {
            AdvancedFillDialog dialog = new AdvancedFillDialog();
            dialog.show(fragmentManager, DIALOG_ADVANCED_FILL);
        }
    }
}

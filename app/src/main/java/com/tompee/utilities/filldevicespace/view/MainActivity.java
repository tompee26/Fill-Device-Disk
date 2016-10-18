package com.tompee.utilities.filldevicespace.view;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.johnpersano.supertoasts.library.Style;
import com.github.johnpersano.supertoasts.library.SuperActivityToast;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.tompee.utilities.filldevicespace.BuildConfig;
import com.tompee.utilities.filldevicespace.R;
import com.tompee.utilities.filldevicespace.controller.Utilities;
import com.tompee.utilities.filldevicespace.controller.storage.StorageUtility;
import com.tompee.utilities.filldevicespace.view.adapter.MainViewPagerAdapter;
import com.tompee.utilities.filldevicespace.view.base.BaseActivity;
import com.tompee.utilities.filldevicespace.view.custom.NonSwipeablePager;

public class MainActivity extends BaseActivity implements ViewPager.OnPageChangeListener {
    public static final String SHARED_PREFERENCE_NAME = "fill_device_disk_shared_prefs";
    public static final String TAG_SD_CARD = "sd_card";
    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final String SHARED_PREF = "filldevicedisksharedpref";
    private static final String LAUNCH_COUNT = "launch_count";
    private static final int MIN_LAUNCH_COUNT = 7;
    private static final String LICENSE_URL = "file:///android_asset/opensource.html";

    private NonSwipeablePager mViewPager;
    private long mSystemSize;

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

        ImageView bg = (ImageView) findViewById(R.id.background);
        bg.setImageDrawable(Utilities.getDrawableFromAsset(this, "bg.jpg"));
        mViewPager = (NonSwipeablePager) findViewById(R.id.pager_main);
        mViewPager.setAdapter(new MainViewPagerAdapter(this, getSupportFragmentManager()));
        mViewPager.addOnPageChangeListener(this);
        mViewPager.setOffscreenPageLimit(mViewPager.getAdapter().getCount());
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout_main);
        tabLayout.setupWithViewPager(mViewPager);
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
    protected void onStart() {
        super.onStart();
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.
                    WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSystemSize = StorageUtility.getTotalStorageSize(this) -
                StorageUtility.getAvailableStorageSize(this) - StorageUtility.getFillSize(this);
    }

    public long getSystemSize() {
        return mSystemSize;
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
                startActivity(Intent.createChooser(intent, getString(R.string.ids_lbl_contact)));
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

    @Override
    public void onBackPressed() {
        if (mViewPager.getInterceptSwipe()) {
            SuperActivityToast.create(this, new Style(), Style.TYPE_STANDARD)
                    .setText(getString(R.string.ids_message_stop_process))
                    .setDuration(Style.DURATION_LONG)
                    .setFrame(Style.FRAME_LOLLIPOP)
                    .setColor(ContextCompat.getColor(this, R.color.button))
                    .setAnimations(Style.ANIMATIONS_POP).show();
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (!(grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Toast.makeText(this, getString(R.string.ids_lbl_permission),
                            Toast.LENGTH_LONG).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void interceptViewPagerTouchEvents(boolean intercept) {
        mViewPager.interceptSwipeEvent(intercept);
    }
}

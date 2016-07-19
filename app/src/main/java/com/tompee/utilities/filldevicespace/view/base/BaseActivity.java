package com.tompee.utilities.filldevicespace.view.base;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;

public class BaseActivity extends FragmentActivity {
    private AppCompatDelegate mDelegate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDelegate = AppCompatDelegate.create(this, null);
        mDelegate.onCreate(savedInstanceState);
    }

    @Override
    public void setContentView(int layoutResID) {
        mDelegate.setContentView(layoutResID);
    }

    @Override
    public void invalidateOptionsMenu() {
        mDelegate.invalidateOptionsMenu();
    }

    protected void setToolbar(int toolbarId, boolean enableHomeButton) {
        Toolbar toolbar = (Toolbar) findViewById(toolbarId);
        mDelegate.setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        mDelegate.getSupportActionBar().setDisplayShowTitleEnabled(false);
        mDelegate.getSupportActionBar().setDisplayHomeAsUpEnabled(enableHomeButton);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU && mDelegate.getSupportActionBar() != null) {
            mDelegate.getSupportActionBar().openOptionsMenu();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }

        return super.onOptionsItemSelected(item);
    }

}

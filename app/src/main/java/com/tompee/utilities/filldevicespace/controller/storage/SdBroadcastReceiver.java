package com.tompee.utilities.filldevicespace.controller.storage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;

public class SdBroadcastReceiver extends BroadcastReceiver {
    public static final int STORAGE_UPDATE_DELAY = 1000;
    public static final String SD_CARD_ACTION = "com.tompee.utilities.filldevicedisk.SD_CARD_ACTION";
    public static final String FILL_ACTION = "com.tompee.utilities.filldevicedisk.FILL_ACTION";
    public static final String CUSTOM_FILL_ACTION = "com.tompee.utilities.filldevicedisk.CUSTOM_FILL_ACTION";
    public static final String EXTRA_SPEED = "speed";
    public static final String EXTRA_PERCENTAGE = "percentage";
    public static final String STORAGE_INTENT_SCHEME = "file";
    private final StorageEventListener mListener;

    public SdBroadcastReceiver(StorageEventListener listener) {
        mListener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case SD_CARD_ACTION:
                mListener.onStorageChange();
                break;
            case FILL_ACTION:
                mListener.onFill(intent.getFloatExtra(EXTRA_SPEED, 0));
                break;
            case CUSTOM_FILL_ACTION:
                mListener.onCustomFill(intent.getFloatExtra(EXTRA_SPEED, 0),
                        intent.getFloatExtra(EXTRA_PERCENTAGE, 0));
                break;
            default:
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                    Handler handler = new Handler();
                    Runnable runnable = new Runnable() {
                        public void run() {
                            mListener.onStorageStateChange();
                        }
                    };
                    handler.postDelayed(runnable, STORAGE_UPDATE_DELAY);
                } else {
                    mListener.onStorageStateChange();
                }
                break;
        }
    }

    public interface StorageEventListener {
        void onStorageChange();

        void onFill(float speed);

        void onCustomFill(float speed, float percentage);

        void onStorageStateChange();
    }
}

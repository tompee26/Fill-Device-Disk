package com.tompee.utilities.filldevicespace.controller.storage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SdBroadcastReceiver extends BroadcastReceiver {
    public static final String SD_CARD_ACTION = "com.tompee.utilities.filldevicedisk.SD_CARD_ACTION";
    public static final String FILL_ACTION = "com.tompee.utilities.filldevicedisk.FILL_ACTION";
    public static final String CUSTOM_FILL_ACTION = "com.tompee.utilities.filldevicedisk.CUSTOM_FILL_ACTION";
    public static final String EXTRA_SPEED = "speed";
    public static final String EXTRA_PERCENTAGE = "percentage";
    private final StorageEventListener mListener;

    public SdBroadcastReceiver(StorageEventListener listener) {
        mListener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(SD_CARD_ACTION)) {
            mListener.onStorageChange();
        } else if (intent.getAction().equals(FILL_ACTION)) {
            mListener.onFill(intent.getFloatExtra(EXTRA_SPEED, 0));
        } else {
            mListener.onCustomFill(intent.getFloatExtra(EXTRA_SPEED, 0),
                    intent.getFloatExtra(EXTRA_PERCENTAGE, 0));
        }
    }

    public interface StorageEventListener {
        void onStorageChange();

        void onFill(float speed);

        void onCustomFill(float speed, float percentage);
    }
}

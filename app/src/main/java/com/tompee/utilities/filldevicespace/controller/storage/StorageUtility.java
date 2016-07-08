package com.tompee.utilities.filldevicespace.controller.storage;

import android.content.Context;
import android.os.Build;
import android.os.StatFs;

public class StorageUtility {

    public static long getAvailableStorageSize(String path) {
        long availableSize;
        StatFs statFs = new StatFs(path);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) { //API 18
            //noinspection deprecation
            long blockSize = statFs.getBlockSize();
            //noinspection deprecation
            availableSize = statFs.getAvailableBlocks() * blockSize;
        } else {
            availableSize = statFs.getAvailableBytes();
        }
        return availableSize;
    }

    public static String getFilesDirectory(Context context) {
        return context.getFilesDir().getAbsolutePath();
    }
}

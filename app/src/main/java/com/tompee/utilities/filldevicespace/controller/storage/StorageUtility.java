package com.tompee.utilities.filldevicespace.controller.storage;

import android.content.Context;
import android.os.Build;
import android.os.StatFs;

import java.io.File;

public class StorageUtility {

    public static long getAvailableStorageSize(Context context) {
        long availableSize;
        String path = getFilesDirectory(context);
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

    public static int getFileCount(Context context) {
        return context.getFilesDir().listFiles().length;
    }

    public static void deleteFiles(Context context) {
        for (File file : context.getFilesDir().listFiles()) {
            file.delete();
        }
    }
}

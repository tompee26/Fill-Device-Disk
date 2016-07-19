package com.tompee.utilities.filldevicespace.controller.storage;

import android.content.Context;
import android.os.Build;
import android.os.StatFs;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class StorageUtility {

    public static long getAvailableStorageSize(Context context) {
        long availableSize;
        StatFs statFs = new StatFs(getFilesDirectory(context));
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

    public static long getTotalStorageSize(Context context) {
        long totalSize;
        StatFs statFs = new StatFs(getFilesDirectory(context));
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) { //API 18
            //noinspection deprecation
            long blockSize = statFs.getBlockSize();
            //noinspection deprecation
            totalSize = statFs.getBlockCount() * blockSize;
        } else {
            totalSize = statFs.getTotalBytes();
        }
        return totalSize;
    }

    public static long getFillSize(Context context) {
        File file = new File(getFilesDirectory(context));

        final List<File> dirs = new LinkedList<>();
        dirs.add(file);

        long result = 0;
        while (!dirs.isEmpty()) {
            final File dir = dirs.remove(0);
            if (!dir.exists())
                continue;
            final File[] listFiles = dir.listFiles();
            if (listFiles == null || listFiles.length == 0)
                continue;
            for (final File child : listFiles) {
                result += child.length();
                if (child.isDirectory())
                    dirs.add(child);
            }
        }
        return result;
    }


    public static String getFilesDirectory(Context context) {
        return context.getFilesDir().getAbsolutePath();
    }

    public static int getFileCount(Context context) {
        return context.getFilesDir().listFiles().length;
    }

    public static void deleteFiles(Context context) {
        for (File file : context.getFilesDir().listFiles()) {
            //noinspection ResultOfMethodCallIgnored
            file.delete();
        }
    }
}

package com.tompee.utilities.filldevicespace.controller.storage;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;

import com.tompee.utilities.filldevicespace.Constants;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class StorageUtility {
    private static final String SECONDARY_STORAGE_TAG = "SECONDARY_STORAGE";

    public static String getRemovableStorage(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            String secondaryStorage = System.getenv(SECONDARY_STORAGE_TAG);
            if (secondaryStorage != null) {
                final String[] paths = secondaryStorage.split(":");
                for (String path : paths) {
                    File file = new File(path);
                    if (file.listFiles() != null) {
                        //noinspection ConstantConditions
                        path = path + context.getExternalFilesDir(null).getAbsolutePath().
                                replace(Environment.getExternalStorageDirectory().getAbsolutePath(), "");
                        File fileInstance = new File(path);
                        if (!fileInstance.exists()) {
                            if (!fileInstance.mkdirs()) {
                                return null;
                            }
                        }
                        return path;
                    }
                }
            }
        } else {
            File[] extDirs = context.getExternalFilesDirs(null);
            File primary = Environment.getExternalStorageDirectory();
            for (File file : extDirs) {
                if (file == null) {
                    continue;
                }
                if (!file.exists()) {
                    continue;
                }
                String extFilePath = file.getAbsolutePath();
                //noinspection ConstantConditions
                if (extFilePath == null || extFilePath.isEmpty()) {
                    continue;
                }
                if (extFilePath.startsWith(primary.getAbsolutePath())) {
                    continue;
                }
                // checks for API 19 and up only since a delay is added for lower APIs in BroadcastReceiver
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState(file))) {
                        continue;
                    }
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    //noinspection deprecation
                    if (!Environment.MEDIA_MOUNTED.equals(Environment.getStorageState(file))) {
                        continue;
                    }
                }
                return extFilePath;
            }
        }
        return null;
    }

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
        SharedPreferences sp = context.getSharedPreferences(Constants.
                SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        if (sp.getBoolean(Constants.TAG_SD_CARD, false)) {
            return getRemovableStorage(context);
        }
        return context.getFilesDir().getAbsolutePath();
    }

    public static int getFileCount(Context context) {
        SharedPreferences sp = context.getSharedPreferences(Constants.
                SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        if (sp.getBoolean(Constants.TAG_SD_CARD, false)) {
            //noinspection ConstantConditions
            return new File(getRemovableStorage(context)).listFiles().length;
        }
        return context.getFilesDir().listFiles().length;
    }

    public static void deleteFiles(Context context) {
        File[] list;
        SharedPreferences sp = context.getSharedPreferences(Constants.
                SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        if (sp.getBoolean(Constants.TAG_SD_CARD, false)) {
            //noinspection ConstantConditions
            list = new File(getRemovableStorage(context)).listFiles();
        } else {
            list = context.getFilesDir().listFiles();
        }
        for (File file : list) {
            //noinspection ResultOfMethodCallIgnored
            file.delete();
        }
    }
}

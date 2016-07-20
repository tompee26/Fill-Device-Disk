package com.tompee.utilities.filldevicespace.controller;

import android.content.Context;

import com.tompee.utilities.filldevicespace.controller.storage.StorageUtility;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Utilities {
    private static final long GB = 1073741824;
    private static final long MB = 1048576;
    private static final long KB = 1024;
    private static final float SPEED_FACTOR = 953.67431640625f;
    private static final float PERCENT = 100;

    public static long convertToBytes(int value, Unit unit) {
        long result;
        switch (unit) {
            case GB:
                result = value * GB;
                break;
            case MB:
                result = value * MB;
                break;
            case KB:
                result = value * KB;
                break;
            default:
                result = 0;
        }
        return result;
    }

    public static float convertToGb(long value) {
        return (float) value / GB;
    }

    public static float getPercentage(float num, float denum) {
        return num / denum * PERCENT;
    }

    public static void copyAssetsFile(Context context, String assetsFileName,
                                      String outputFileName) throws IOException {
        InputStream inputStream = context.getAssets().open(assetsFileName);
        String outputPath = StorageUtility.getFilesDirectory(context);
        //noinspection ResultOfMethodCallIgnored
        new java.io.File(outputPath).mkdirs();
        OutputStream outputStream =
                new FileOutputStream(new java.io.File(outputPath, outputFileName));
        byte data[] = new byte[(int) Utilities.KB];
        int count;
        while ((count = inputStream.read(data)) != -1) {
            outputStream.write(data, 0, count);
        }
        outputStream.flush();
        outputStream.close();
        inputStream.close();
    }

    public static float computeSpeed(float assetSize, float timeElapsed) {
        return assetSize / timeElapsed * SPEED_FACTOR;
    }

    public enum Unit {
        GB("GB"),
        MB("MB"),
        KB("KB");

        private final String mText;

        Unit(final String text) {
            mText = text;
        }

        @Override
        public final String toString() {
            return mText;
        }

    }
}

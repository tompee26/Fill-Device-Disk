package com.tompee.utilities.filldevicespace.controller;

import android.content.Context;

import com.tompee.utilities.filldevicespace.controller.storage.StorageUtility;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Utilities {
    private static final float SPEED_FACTOR = 953.67431640625f;

    public static void copyAssetsFile(Context context, String assetsFileName,
                                      String outputFileName) throws IOException {
        InputStream inputStream = context.getAssets().open(assetsFileName);
        String outputPath = StorageUtility.getFilesDirectory(context);
        //noinspection ResultOfMethodCallIgnored
        new java.io.File(outputPath).mkdirs();
        FileOutputStream outputStream =
                new FileOutputStream(new java.io.File(outputPath, outputFileName));
        byte data[] = new byte[4096];
        int count;
        while ((count = inputStream.read(data)) != -1) {
            outputStream.write(data, 0, count);
        }
        outputStream.flush();
        outputStream.getFD().sync();
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

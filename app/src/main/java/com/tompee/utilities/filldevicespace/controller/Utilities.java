package com.tompee.utilities.filldevicespace.controller;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.tompee.utilities.filldevicespace.controller.storage.StorageUtility;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

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

    public static Drawable getDrawableFromAsset(Context context, String filename) {
        Drawable drawable;
        try {
            InputStream ims = context.getAssets().open(filename);
            drawable = Drawable.createFromStream(ims, null);
        } catch (IOException ex) {
            drawable = null;
        }
        return drawable;
    }

    public static String getStringFromAsset(Context context, String filename) {
        StringBuilder buffer = new StringBuilder();
        InputStream inputStream;
        try {
            inputStream = context.getAssets().open(filename);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String str;
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer.toString();
    }
}

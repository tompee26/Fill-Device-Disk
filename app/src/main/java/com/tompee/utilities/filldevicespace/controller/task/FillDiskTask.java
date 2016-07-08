package com.tompee.utilities.filldevicespace.controller.task;

import android.content.Context;
import android.os.AsyncTask;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FillDiskTask extends AsyncTask<Void, Void, Void> {
    private static final String ASSET_FILE_NAME = "filler";
    private static final int BLOCK_SIZE = 1024;
    private final Context mContext;
    private final OnFillDiskSpaceListener mListener;
    private int mFileCount;

    public FillDiskTask(Context context, OnFillDiskSpaceListener listener) {
        mContext = context;
        mListener = listener;
    }

    @Override
    protected Void doInBackground(Void... params) {
        while (!isCancelled()) {
            try {
                copyAssetsFile(mContext, ASSET_FILE_NAME, ASSET_FILE_NAME + mFileCount);
                mFileCount++;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private void copyAssetsFile(Context context, String assetsFileName,
                                String outputFileName) throws IOException {
        InputStream inputStream = context.getAssets().open(assetsFileName);
        String outputPath = context.getFilesDir().getPath();
        //noinspection ResultOfMethodCallIgnored
        new java.io.File(outputPath).mkdirs();
        OutputStream outputStream =
                new FileOutputStream(new java.io.File(outputPath, outputFileName));

        byte data[] = new byte[BLOCK_SIZE];
        int count;
        while ((count = inputStream.read(data)) != -1) {
            outputStream.write(data, 0, count);
        }

        outputStream.flush();
        outputStream.close();
        inputStream.close();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        mListener.onFillDiskSpaceComplete();
    }

    public interface OnFillDiskSpaceListener {
        void onFillDiskSpaceComplete();
    }
}

package com.tompee.utilities.filldevicespace.controller.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.tompee.utilities.filldevicespace.controller.storage.StorageUtility;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FillDiskTask extends AsyncTask<Void, Long, Void> {
    private static final String TAG = "FillDiskTask";
    private static final String ASSET_NULL = "";
    private static final String ASSET_5MB = "filler_5MB";
    private static final String ASSET_1MB = "filler_1MB";
    private static final String ASSET_100KB = "filler_100KB";
    private static final String ASSET_8KB = "filler_8KB";
    private static final String ASSET_1KB = "filler_1KB";
    private static final int BLOCK_SIZE = 1024;
    private static final int PERCENT = 100;
    private final Context mContext;
    private final OnFillDiskSpaceListener mListener;
    private final long mInitialDiskSpace;

    public FillDiskTask(Context context, OnFillDiskSpaceListener listener) {
        mContext = context;
        mListener = listener;
        mInitialDiskSpace = StorageUtility.getAvailableStorageSize(mContext);
    }

    @Override
    protected Void doInBackground(Void... params) {
        int fileCount = StorageUtility.getFileCount(mContext);
        String currentAsset = determineAsset(ASSET_NULL);
        while (!isCancelled()) {
            try {
                copyAssetsFile(mContext, currentAsset, currentAsset + fileCount);
                fileCount++;
                long current = StorageUtility.getAvailableStorageSize(mContext);
                float progress = ((float) (mInitialDiskSpace - current) /
                        (float) mInitialDiskSpace * PERCENT);
                publishProgress(current, (long) (progress));
            } catch (IOException e) {
                currentAsset = determineAsset(currentAsset);
                Log.d(TAG, "current asset: " + currentAsset);
                if (currentAsset == null) {
                    break;
                }
            }
        }
        return null;
    }

    private String determineAsset(String currentAsset) {
        switch (currentAsset) {
            case ASSET_NULL:
                return ASSET_5MB;
            case ASSET_5MB:
                return ASSET_1MB;
            case ASSET_1MB:
                return ASSET_100KB;
            case ASSET_100KB:
                return ASSET_8KB;
            case ASSET_8KB:
                return ASSET_1KB;
            case ASSET_1KB:
                break;
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Long... values) {
        mListener.onProgressUpdate(values[0], values[1].intValue());
    }

    private void copyAssetsFile(Context context, String assetsFileName,
                                String outputFileName) throws IOException {
        InputStream inputStream = context.getAssets().open(assetsFileName);
        String outputPath = StorageUtility.getFilesDirectory(mContext);
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

    @Override
    protected void onCancelled() {
        mListener.onCancelled();
    }

    public interface OnFillDiskSpaceListener {
        void onFillDiskSpaceComplete();

        void onProgressUpdate(long current, int progress);

        void onCancelled();
    }
}

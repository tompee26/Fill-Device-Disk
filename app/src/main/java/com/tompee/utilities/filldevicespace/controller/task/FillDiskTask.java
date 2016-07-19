package com.tompee.utilities.filldevicespace.controller.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.tompee.utilities.filldevicespace.controller.storage.StorageUtility;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FillDiskTask extends AsyncTask<Void, Float, Void> {
    private static final String TAG = "FillDiskTask";
    private static final String ASSET_NULL = "";
    private static final int ASSET_NULL_SIZE = 0;
    private static final String ASSET_5MB = "filler_5MB";
    private static final int ASSET_5MB_SIZE = 5242880;
    private static final String ASSET_1MB = "filler_1MB";
    private static final int ASSET_1MB_SIZE = 1048576;
    private static final String ASSET_100KB = "filler_100KB";
    private static final int ASSET_100KB_SIZE = 102400;
    private static final String ASSET_8KB = "filler_8KB";
    private static final int ASSET_8KB_SIZE = 8192;
    private static final String ASSET_1KB = "filler_1KB";
    private static final int ASSET_1KB_SIZE = 1024;
    private static final int BLOCK_SIZE = 1024;
    private static final int PERCENT = 100;
    private static final float SPEED_FACTOR = 953.67431640625f;
    private static final float FILL_FACTOR = 1073741824;
    private static final int SLEEP_INTERVAL = 1000;
    private final Context mContext;
    private final FillDiskSpaceListener mListener;
    private long mTotalDiskSpace;
    private boolean mIsPaused;

    public FillDiskTask(Context context, FillDiskSpaceListener listener) {
        mContext = context;
        mListener = listener;
    }

    @Override
    protected void onPreExecute() {
        mTotalDiskSpace = StorageUtility.getTotalStorageSize(mContext);
        mListener.onPreExecuteUpdate(mTotalDiskSpace);
    }

    @Override
    protected Void doInBackground(Void... params) {
        int fileCount = StorageUtility.getFileCount(mContext);
        String currentAsset = determineAsset(ASSET_NULL);
        while (!isCancelled()) {
            try {
                if (mIsPaused) {
                    Thread.sleep(SLEEP_INTERVAL);
                    continue;
                }
                long start = System.nanoTime();
                copyAssetsFile(mContext, currentAsset, currentAsset + fileCount);
                long timeElapsed = System.nanoTime() - start;
                fileCount++;
                long current = StorageUtility.getAvailableStorageSize(mContext);
                float totalProgress = ((float) (mTotalDiskSpace - current) /
                        (float) mTotalDiskSpace * PERCENT);
                publishProgress(totalProgress, (((float) determineAssetSize(currentAsset) /
                        (float) timeElapsed)), (float) StorageUtility.getFillSize(mContext) /
                        FILL_FACTOR, (float) (StorageUtility.getAvailableStorageSize(mContext))
                        / FILL_FACTOR);
            } catch (IOException e) {
                currentAsset = determineAsset(currentAsset);
                if (currentAsset == null) {
                    break;
                }
            } catch (InterruptedException e) {
                Log.d(TAG, "Interrupted");
            }
        }
        return null;
    }

    private long determineAssetSize(String currentAsset) {
        switch (currentAsset) {
            case ASSET_NULL:
                return ASSET_NULL_SIZE;
            case ASSET_5MB:
                return ASSET_5MB_SIZE;
            case ASSET_1MB:
                return ASSET_1MB_SIZE;
            case ASSET_100KB:
                return ASSET_100KB_SIZE;
            case ASSET_8KB:
                return ASSET_8KB_SIZE;
            case ASSET_1KB:
                break;
        }
        return ASSET_1KB_SIZE;
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
    protected void onProgressUpdate(Float... values) {
        mListener.onProgressUpdate(values[0].intValue(), values[1] * SPEED_FACTOR, values[2], values[3]);
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

    public void pause() {
        mIsPaused = true;
    }

    public void resume() {
        mIsPaused = false;
    }

    public boolean isRunning() {
        return !mIsPaused;
    }

    public interface FillDiskSpaceListener {
        void onFillDiskSpaceComplete();

        void onPreExecuteUpdate(long total);

        void onProgressUpdate(int totalProgress, float speed, float fillSize, float free);

        void onCancelled();
    }
}

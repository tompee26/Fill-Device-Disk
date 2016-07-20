package com.tompee.utilities.filldevicespace.controller.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.tompee.utilities.filldevicespace.FillDeviceDiskApp;
import com.tompee.utilities.filldevicespace.controller.Utilities;
import com.tompee.utilities.filldevicespace.controller.storage.StorageUtility;

import java.io.IOException;

public class FillDiskTask extends AsyncTask<Boolean, Float, Void> {
    private static final String TAG = "FillDiskTask";
    private static final int SLEEP_INTERVAL = 1000;
    private final Context mContext;
    private final FillDiskTaskListener mListener;
    private final long mLimit;
    private long mTotalDiskSpace;
    private boolean mIsPaused;

    public FillDiskTask(Context context, FillDiskTaskListener listener, long limit) {
        mContext = context;
        mListener = listener;
        mLimit = limit;
    }

    @Override
    protected void onPreExecute() {
        mTotalDiskSpace = StorageUtility.getTotalStorageSize(mContext);
        mListener.onPreExecuteUpdate(mTotalDiskSpace);
    }

    @Override
    protected Void doInBackground(Boolean... params) {
        if (mLimit != 0) {
            performFill(mLimit, params[0]);
        } else {
            performFill(params[0]);
        }
        return null;
    }

    private void performFill(boolean withChart) {
        int fileCount = StorageUtility.getFileCount(mContext);
        FillDeviceDiskApp app = (FillDeviceDiskApp) mContext.getApplicationContext();
        String currentAsset = app.getAsset(StorageUtility.getAvailableStorageSize(mContext));
        while (!isCancelled()) {
            try {
                if (mIsPaused) {
                    Thread.sleep(SLEEP_INTERVAL);
                    continue;
                }
                long start = System.nanoTime();
                Utilities.copyAssetsFile(mContext, currentAsset, currentAsset + fileCount);
                long timeElapsed = System.nanoTime() - start;
                fileCount++;
                float totalProgress = Utilities.getPercentage(mTotalDiskSpace - StorageUtility.
                        getAvailableStorageSize(mContext), mTotalDiskSpace);
                if (withChart) {
                    float speed = Utilities.computeSpeed(app.getAssetSize(currentAsset), timeElapsed);
                    publishProgress(totalProgress, speed,
                            Utilities.convertToGb(StorageUtility.getFillSize(mContext)),
                            Utilities.convertToGb(StorageUtility.getAvailableStorageSize(mContext)));
                } else {
                    publishProgress(totalProgress);
                }
            } catch (IOException e) {
                currentAsset = app.getAsset(StorageUtility.getAvailableStorageSize(mContext));
                if (currentAsset == null) {
                    break;
                }
            } catch (InterruptedException e) {
                Log.d(TAG, "Interrupted");
            }
        }
    }

    private void performFill(long limit, boolean withChart) {
        int fileCount = StorageUtility.getFileCount(mContext);
        FillDeviceDiskApp app = (FillDeviceDiskApp) mContext.getApplicationContext();
        long fillSize = 0;
        while (fillSize < limit && !isCancelled()) {
            try {
                if (mIsPaused) {
                    Thread.sleep(SLEEP_INTERVAL);
                    continue;
                }
                String asset = app.getAsset(limit - fillSize);
                if (asset == null) {
                    break;
                }
                long start = System.nanoTime();
                Utilities.copyAssetsFile(mContext, asset, asset + fileCount);
                fillSize += app.getAssetSize(asset);
                long timeElapsed = System.nanoTime() - start;
                fileCount++;
                float totalProgress = Utilities.getPercentage(fillSize, limit);
                Log.d(TAG, "progress:" + totalProgress);
                if (withChart) {
                    float speed = Utilities.computeSpeed(app.getAssetSize(asset), timeElapsed);
                    publishProgress(totalProgress, speed,
                            Utilities.convertToGb(StorageUtility.getFillSize(mContext)),
                            Utilities.convertToGb(StorageUtility.getAvailableStorageSize(mContext)));
                } else {
                    publishProgress(totalProgress);
                }
            } catch (InterruptedException e) {
                Log.d(TAG, "Interrupted");
            } catch (IOException e) {
                break;
            }
        }
    }

    @Override
    protected void onProgressUpdate(Float... values) {
        if (values.length > 1) {
            mListener.onProgressUpdate(values[0].intValue(), values[1], values[2], values[3]);
        } else {
            mListener.onProgressUpdate(values[0].intValue());
        }
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

    public interface FillDiskTaskListener {
        void onFillDiskSpaceComplete();

        void onPreExecuteUpdate(long total);

        void onProgressUpdate(int totalProgress, float speed, float fillSize, float free);

        void onProgressUpdate(int totalProgress);

        void onCancelled();
    }
}

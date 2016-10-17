package com.tompee.utilities.filldevicespace.controller.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.tompee.utilities.filldevicespace.FillDeviceDiskApp;
import com.tompee.utilities.filldevicespace.controller.Utilities;
import com.tompee.utilities.filldevicespace.controller.storage.StorageUtility;

import java.io.IOException;

public class FillDiskTask extends AsyncTask<Void, Float, Void> {
    private static final String TAG = "FillDiskTask";
    private static final int SLEEP_INTERVAL = 10;
    private final Context mContext;
    private final FillDiskTaskListener mListener;
    private final long mLimit;

    public FillDiskTask(Context context, FillDiskTaskListener listener, long limit) {
        mContext = context;
        mListener = listener;
        mLimit = limit;
    }

    @Override
    protected Void doInBackground(Void... params) {
        if (mLimit != 0) {
            performFill(mLimit);
        } else {
            performFill();
        }
        return null;
    }

    private void performFill() {
        int fileCount = StorageUtility.getFileCount(mContext);
        FillDeviceDiskApp app = (FillDeviceDiskApp) mContext.getApplicationContext();
        String currentAsset = app.getAsset(StorageUtility.getAvailableStorageSize(mContext));
        while (!isCancelled()) {
            try {
                Thread.sleep(SLEEP_INTERVAL);
                long start = System.nanoTime();
                Utilities.copyAssetsFile(mContext, currentAsset, currentAsset + fileCount);
                long timeElapsed = System.nanoTime() - start;
                fileCount++;
                float speed = Utilities.computeSpeed(app.getAssetSize(currentAsset), timeElapsed);
                publishProgress(speed);
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

    private void performFill(long limit) {
        int fileCount = StorageUtility.getFileCount(mContext);
        FillDeviceDiskApp app = (FillDeviceDiskApp) mContext.getApplicationContext();
        long fillSize = 0;
        while (fillSize < limit && !isCancelled()) {
            try {
                Thread.sleep(SLEEP_INTERVAL);
                String asset = app.getAsset(limit - fillSize);
                if (asset == null) {
                    break;
                }
                long start = System.nanoTime();
                Utilities.copyAssetsFile(mContext, asset, asset + fileCount);
                fillSize += app.getAssetSize(asset);
                long timeElapsed = System.nanoTime() - start;
                fileCount++;
                float speed = Utilities.computeSpeed(app.getAssetSize(asset), timeElapsed);
                publishProgress(speed);
            } catch (InterruptedException e) {
                Log.d(TAG, "Interrupted");
            } catch (IOException e) {
                break;
            }
        }
    }

    @Override
    protected void onProgressUpdate(Float... values) {
        mListener.onProgressUpdate(values[0]);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        mListener.onFillDiskSpaceComplete();
    }

    @Override
    protected void onCancelled() {
        mListener.onCancelled();
    }

    public interface FillDiskTaskListener {
        void onFillDiskSpaceComplete();

        void onProgressUpdate(float speed);

        void onCancelled();
    }
}

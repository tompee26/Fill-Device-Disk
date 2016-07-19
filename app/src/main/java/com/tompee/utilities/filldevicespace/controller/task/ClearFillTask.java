package com.tompee.utilities.filldevicespace.controller.task;

import android.content.Context;
import android.os.AsyncTask;

import com.tompee.utilities.filldevicespace.controller.storage.StorageUtility;

public class ClearFillTask extends AsyncTask<Void, Void, Long> {
    private final ClearFillListener mListener;
    private final Context mContext;

    public ClearFillTask(Context context, ClearFillListener listener) {
        mContext = context;
        mListener = listener;
    }

    @Override
    protected Long doInBackground(Void... params) {
        long before = StorageUtility.getAvailableStorageSize(mContext);
        StorageUtility.deleteFiles(mContext);
        return StorageUtility.getAvailableStorageSize(mContext) - before;
    }

    @Override
    protected void onPostExecute(Long cleared) {
        mListener.onFinish(cleared);
    }

    public interface ClearFillListener {
        void onFinish(long clearedSpace);
    }
}

package com.tompee.utilities.filldevicespace.controller.task;

import android.content.Context;
import android.os.AsyncTask;

import com.tompee.utilities.filldevicespace.controller.storage.StorageUtility;

public class ClearFillTask extends AsyncTask<Void, Void, Void> {
    private final ClearFillListener mListener;
    private final Context mContext;

    public ClearFillTask(Context context, ClearFillListener listener) {
        mContext = context;
        mListener = listener;
    }

    @Override
    protected Void doInBackground(Void... params) {
        StorageUtility.deleteFiles(mContext);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        mListener.onFinish();
    }

    public interface ClearFillListener {
        void onFinish();
    }
}

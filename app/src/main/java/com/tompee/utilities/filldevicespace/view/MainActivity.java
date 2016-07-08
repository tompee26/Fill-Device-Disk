package com.tompee.utilities.filldevicespace.view;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StatFs;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.tompee.utilities.filldevicespace.R;
import com.tompee.utilities.filldevicespace.view.base.BaseActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private FillTask mFillTask;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top);
        setToolbar(R.id.toolbar, false);
        TextView title = (TextView) findViewById(R.id.toolbar_text);
        title.setText(R.string.app_name);

        View view = findViewById(R.id.fill);
        view.setOnClickListener(this);
        view = findViewById(R.id.delete);
        view.setOnClickListener(this);
        view.setEnabled(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fill:
                if (mFillTask == null) {
                    mFillTask = new FillTask(this, (TextView) findViewById(R.id.textview));
                    mFillTask.execute();
                } else {
                    mFillTask.cancel(true);
                    mFillTask = null;
                }
                break;
            case R.id.delete:
                deleteFillingFiles();
                TextView tv = (TextView) findViewById(R.id.textview);
                setDiskSpaceText(tv, getAvailableStorageSize(getFilesDir().getAbsolutePath()));
                break;
        }
    }

    private void deleteFillingFiles() {
        File dir = new File(getFilesDir().getAbsolutePath());
        for (File file : dir.listFiles()) {
            file.delete();
        }
    }

    protected void setDiskSpaceText(TextView tv, long size) {
        tv.setText("File size: " + Formatter.formatShortFileSize(this, size));
    }

    public long getAvailableStorageSize(String path) {
        long availableSize;
        StatFs statFs = new StatFs(path);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) { //API 18
            //noinspection deprecation
            long blockSize = statFs.getBlockSize();
            //noinspection deprecation
            availableSize = statFs.getAvailableBlocks() * blockSize;
        } else {
            availableSize = statFs.getAvailableBytes();
        }
        return availableSize;
    }

    private class FillTask extends AsyncTask<Void, Void, Void> {
        private Context mContext;
        private int mFileCount;
        private TextView mTextView;
        private long mSize;

        public FillTask(Context context, TextView tv) {
            mContext = context;
            mTextView = tv;
        }

        @Override
        protected Void doInBackground(Void... params) {
            while (!isCancelled()) {
                try {
                    copyAssetsFile(mContext, "file.jpg", "file" + mFileCount + ".jpg");
                    mFileCount++;
                    mSize = getAvailableStorageSize(mContext.getFilesDir().getAbsolutePath());
                    publishProgress();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            setDiskSpaceText(mTextView, mSize);
        }

        private void copyAssetsFile(Context context, String assetsFileName,
                                    String outputFileName) throws IOException {
            InputStream inputStream = context.getAssets().open(assetsFileName);
            String outputPath = context.getFilesDir().getPath();
            //noinspection ResultOfMethodCallIgnored
            new java.io.File(outputPath).mkdirs();
            OutputStream outputStream =
                    new FileOutputStream(new java.io.File(outputPath, outputFileName));

            byte data[] = new byte[1024];
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
            Button button = (Button) findViewById(R.id.fill);
            button.setText("Fill Device");
            mFillTask = null;
        }
    }

}

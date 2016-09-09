package com.tompee.utilities.filldevicespace;

import android.app.Application;

import com.google.android.gms.ads.MobileAds;

import java.util.LinkedHashMap;
import java.util.Map;

public class FillDeviceDiskApp extends Application {
    private static final String ASSET_5MB = "filler_5MB";
    private static final String ASSET_1MB = "filler_1MB";
    private static final String ASSET_100KB = "filler_100KB";
    private static final String ASSET_8KB = "filler_8KB";
    private static final long ASSET_5MB_SIZE = 5242880;
    private static final long ASSET_1MB_SIZE = 1048576;
    private static final long ASSET_100KB_SIZE = 102400;
    private static final long ASSET_8KB_SIZE = 8192;
    private static final String ADMOB_APP_ID = "ca-app-pub-1411804566429951~9732472227";
    private static Map<String, Long> mAssetMap;

    @Override
    public void onCreate() {
        super.onCreate();
        mAssetMap = new LinkedHashMap<>();
        mAssetMap.put(ASSET_5MB, ASSET_5MB_SIZE);
        mAssetMap.put(ASSET_1MB, ASSET_1MB_SIZE);
        mAssetMap.put(ASSET_100KB, ASSET_100KB_SIZE);
        mAssetMap.put(ASSET_8KB, ASSET_8KB_SIZE);
        MobileAds.initialize(getApplicationContext(), ADMOB_APP_ID);
    }

    public String getAsset(long limit) {
        for (String key : mAssetMap.keySet()) {
            if (mAssetMap.get(key) <= limit) {
                return key;
            }
        }
        return null;
    }

    public long getAssetSize(String asset) {
        return mAssetMap.get(asset);
    }
}

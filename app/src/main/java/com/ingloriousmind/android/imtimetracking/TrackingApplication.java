package com.ingloriousmind.android.imtimetracking;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.util.Log;

import com.ingloriousmind.android.imtimetracking.persistence.DbHelper;
import com.ingloriousmind.android.imtimetracking.util.FileUtil;

/**
 * IM timetracking application
 *
 * @author lavong.soysavanh
 */
public class TrackingApplication extends Application {

    public static final String TAG = TrackingApplication.class.getSimpleName();

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate() {
        super.onCreate();

        Log.v(TAG, "initializing...");

        Log.v(TAG, "  reading config");
        Resources res = getResources();
        Config.debug = res.getBoolean(R.bool.debug);
        try {
            PackageInfo pi = getPackageManager().getPackageInfo(getPackageName(), 0);
            Config.versionCode = pi.versionCode;
            Config.versionName = pi.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.w(TAG, "failed reading package versions", e);
        }

        Log.v(TAG, "  establishing folder structure");
        FileUtil.appDir = getExternalFilesDir(null);
        if (FileUtil.appDir != null) {
            FileUtil.appDir.mkdirs();
            Log.v(TAG, "    " + FileUtil.appDir.getAbsolutePath());
        }

        Log.v(TAG, "  initializing persistence manager");
        DbHelper.initialize(this);

        Log.v(TAG, "... done.");

        if (Config.debug) {
            Config.dump();
        }
    }

}

package com.ingloriousmind.android.imtimetracking;

import android.app.Application;
import android.util.Log;

import com.ingloriousmind.android.imtimetracking.persistence.DbHelper;
import com.ingloriousmind.android.imtimetracking.util.FileUtil;

import timber.log.Timber;

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

        Log.v(TAG, "  establishing folder structure");
        FileUtil.appDir = getExternalFilesDir(null);
        if (FileUtil.appDir != null) {
            FileUtil.appDir.mkdirs();
            Log.v(TAG, "    " + FileUtil.appDir.getAbsolutePath());
        }

        Log.v(TAG, "  initializing logger");
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        Log.v(TAG, "  initializing persistence manager");
        DbHelper.initialize(this);

        Log.v(TAG, "... done.");
    }

}

package com.ingloriousmind.android.imtimetracking;

import android.app.Application;

import com.ingloriousmind.android.imtimetracking.util.FileUtil;

import timber.log.Timber;

/**
 * IM timetracking application
 *
 * @author lavong.soysavanh
 */
public class TrackingApplication extends Application {

    /**
     * dagger tracking component
     */
    private TrackingComponent component;

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        component = DaggerTrackingComponent.builder().trackingModule(new TrackingModule(this)).build();

        FileUtil.appDir = getExternalFilesDir(null);
        if (FileUtil.appDir != null && FileUtil.appDir.canWrite()) {
            FileUtil.appDir.mkdirs();
        } else {
            FileUtil.appDir = getFilesDir();
        }
        Timber.i("app dir: %s", FileUtil.appDir.getAbsolutePath());
    }

    public TrackingComponent getComponent() {
        return component;
    }

}

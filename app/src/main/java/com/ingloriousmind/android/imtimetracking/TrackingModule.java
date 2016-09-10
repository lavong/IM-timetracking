package com.ingloriousmind.android.imtimetracking;

import android.content.Context;

import com.ingloriousmind.android.imtimetracking.export.Exporter;
import com.ingloriousmind.android.imtimetracking.export.PdfExporter;
import com.ingloriousmind.android.imtimetracking.persistence.DbHelper;
import com.ingloriousmind.android.imtimetracking.time.Tracker;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * tracking module
 *
 * @author lavong.soysavanh
 */
@Module
public class TrackingModule {

    private Context context;

    public TrackingModule(Context context) {
        this.context = context;
    }

    @Provides
    Context provideContext() {
        return context;
    }

    @Provides
    @Singleton
    DbHelper provideDbHelper(Context context) {
        return new DbHelper(context);
    }

    @Singleton
    @Provides
    public Tracker provideTracker(DbHelper dbHelper) {
        return new Tracker(dbHelper);
    }

    @Provides
    public Exporter provideExporter(Context context, Tracker tracker) {
        return new PdfExporter(context, tracker);
    }

}

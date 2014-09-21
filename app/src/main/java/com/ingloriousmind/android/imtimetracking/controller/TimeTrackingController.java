package com.ingloriousmind.android.imtimetracking.controller;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.text.TextUtils;
import android.util.Log;

import com.ingloriousmind.android.imtimetracking.Config;
import com.ingloriousmind.android.imtimetracking.R;
import com.ingloriousmind.android.imtimetracking.controller.task.TimeTrackerTask;
import com.ingloriousmind.android.imtimetracking.model.Tracking;
import com.ingloriousmind.android.imtimetracking.persistence.DbHelper;
import com.ingloriousmind.android.imtimetracking.util.L;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;

/**
 * time tracking controller
 *
 * @author lavong.soysavanh
 */
public class TimeTrackingController {

    /**
     * log tag
     */
    public static final String TAG = TimeTrackingController.class.getSimpleName();

    /**
     * default timer period
     */
    private static final long TIMER_PERIOD = 1000;
    /**
     * time tracking
     */
    private static Tracking tracking;
    /**
     * timer
     */
    private static Timer timer;
    /**
     * listener
     */
    private static TimeTrackerTask.TimeTrackingAware listener;

    /**
     * @see #start(com.ingloriousmind.android.imtimetracking.model.Tracking, com.ingloriousmind.android.imtimetracking.controller.task.TimeTrackerTask.TimeTrackingAware)
     */
    public static Tracking start(TimeTrackerTask.TimeTrackingAware listener) {
        return start(new Tracking(), listener);
    }

    /**
     * starts time tracking and returns the timer task being executed periodically ({@link #TIMER_PERIOD}).
     *
     * @param t                a {@link com.ingloriousmind.android.imtimetracking.model.Tracking} to start or continue
     * @param trackingListener a time tracking tick listener to register
     * @return {@link com.ingloriousmind.android.imtimetracking.model.Tracking}
     */
    public static Tracking start(Tracking t, TimeTrackerTask.TimeTrackingAware trackingListener) {
        listener = trackingListener;
        long now = System.currentTimeMillis();
        if (t == null) {
            tracking = new Tracking();
            tracking.setCreated(now);
        } else {
            tracking = t;
        }
        if (!tracking.isTracking())
            tracking.setLastTrackingStarted(now);
        tracking.setTracking(true);
        storeTracking(tracking);
        stopTimer();
        timer = new Timer(true);
        timer.schedule(new TimeTrackerTask(listener, tracking.getLastTrackingStarted(), tracking.getDuration()), 0, TIMER_PERIOD);
        L.v(TAG, "started: " + tracking.toString());
        return tracking;
    }

    /**
     * stops/pauses time tracking
     *
     * @param keepRunningFlag whether to keep {@link com.ingloriousmind.android.imtimetracking.model.Tracking#tracking} set to true
     * @return current tracking
     */
    public static Tracking stop(boolean keepRunningFlag) {
        if (timer != null && tracking != null) {
            long now = System.currentTimeMillis();
            tracking.setDuration(now - tracking.getLastTrackingStarted() + tracking.getDuration());
            tracking.setTracking(keepRunningFlag);
            if (keepRunningFlag)
                tracking.setLastTrackingStarted(now);
            storeTracking(tracking);
            stopTimer();
            L.v(TAG, "stopped: " + tracking);
            return tracking;
        }
        return null;
    }

    /**
     * stops time tracking
     *
     * @return current tracking
     */
    public static Tracking stop() {
        return stop(false);
    }

    /**
     * cancels timer
     */
    private static void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
    }


    public static List<Tracking> fetchTrackings() {
        List<Tracking> trackings = DbHelper.getInstance().fetchTrackings();
        if (Config.debug) {
            L.d(TAG, "fetched " + trackings.size() + " trackings:");
            for (Tracking t : trackings)
                L.v(TAG, "  " + t.toString());
        }
        return trackings;
    }

    public static Tracking fetchMostRecentTracking() {
        Tracking mostRecentTracking = DbHelper.getInstance().fetchMostRecentTracking();
        L.d(TAG, "most recent: " + mostRecentTracking);
        return mostRecentTracking;
    }

    public static void removeTracking(Tracking t) {
        if (t == null)
            return;

        boolean deleted = DbHelper.getInstance().removeTracking(t);

        if (deleted)
            L.d(TAG, "removed " + t.toString());
        else
            L.w(TAG, "unable to remove: " + t.toString());
    }

    public static void storeTracking(Tracking t) {
        if (t == null)
            return;

        boolean stored = DbHelper.getInstance().storeTracking(t);

        if (stored)
            L.d(TAG, "stored " + t.toString());
        else
            L.w(TAG, "unable to store: " + t.toString());
    }


    public static File exportPdf(Context ctx) {

        // get trackings
        List<Tracking> trackings = fetchTrackings();
        if (trackings == null || trackings.isEmpty())
            return null;
        L.d(TAG, "exporting " + trackings.size() + " trackings to pdf");

        // create file
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String todayString = sdf.format(new Date(System.currentTimeMillis()));
        String pdfFileName = "im-timetracking-" + todayString + ".pdf";
        File appsPrivateDir = ctx.getExternalFilesDir(null);
        appsPrivateDir.mkdirs();
        File pdfFile = new File(appsPrivateDir, pdfFileName);

        // create pdf
        Paint p = new Paint();
        p.setTextSize(10);
        p.setColor(Color.BLACK);
        PdfDocument doc = new PdfDocument();
        int a4Width = (int) (210 / 25.4 * 72);
        int a4Height = (int) (297 / 25.4 * 72);
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(a4Width, a4Height, 1).create();
        PdfDocument.Page page = doc.startPage(pageInfo);

        // write pdf entries
        Canvas c = page.getCanvas();
        int lineHeight = 20;
        int leftMargin = 5;
        StringBuffer sb = new StringBuffer();
        String unnamed = ctx.getString(R.string.list_item_tracking_unnamed_title);
        for (int i = 0; i < trackings.size(); i++) {
            Tracking t = trackings.get(i);
            sb.setLength(0);
            sb.append(sdf.format(new Date(t.getCreated()))).append(" ");
            int minutes = (int) t.getDuration() / 60 / 1000;
            sb.append(minutes / 60).append("h ").append(minutes % 60).append("m | ");
            sb.append(TextUtils.isEmpty(t.getTitle()) ? unnamed : t.getTitle());
            c.drawText(sb.toString(), leftMargin, (i + 1) * lineHeight, p);
        }
        doc.finishPage(page);

        try {
            // write to file
            L.v(TAG, "writing pdf file " + pdfFile.getAbsolutePath());
            doc.writeTo(new FileOutputStream(pdfFile));
        } catch (IOException e) {
            Log.e(TAG, "failed writing pdf file", e);
            return null;
        } finally {
            doc.close();
        }

        return pdfFile;
    }

}

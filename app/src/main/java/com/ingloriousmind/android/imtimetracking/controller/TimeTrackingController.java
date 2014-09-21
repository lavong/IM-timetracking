package com.ingloriousmind.android.imtimetracking.controller;

import com.ingloriousmind.android.imtimetracking.Config;
import com.ingloriousmind.android.imtimetracking.controller.task.TimeTrackerTask;
import com.ingloriousmind.android.imtimetracking.model.Tracking;
import com.ingloriousmind.android.imtimetracking.persistence.DbHelper;
import com.ingloriousmind.android.imtimetracking.util.L;

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
     * resumes time tracking
     *
     * @return the timer task
     */
    public static Tracking resume() {
        stopTimer();
        return start(tracking, listener);
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


}

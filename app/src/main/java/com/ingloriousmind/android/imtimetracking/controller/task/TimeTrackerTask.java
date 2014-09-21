package com.ingloriousmind.android.imtimetracking.controller.task;

import android.text.format.DateUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TimerTask;

/**
 * time tracking timer task
 *
 * @author lavong.soysavanh
 */
public class TimeTrackerTask extends TimerTask {

    /**
     * time tracking listener callback
     */
    public interface TimeTrackingAware {
        /**
         * callback triggered by timer task
         *
         * @param elapsedTime elapsed time as formatted string
         * @param duration    duration
         */
        void onTick(String elapsedTime, long duration);
    }

    private long start;
    private long offset;
    private StringBuilder sb = new StringBuilder();
    private List<TimeTrackingAware> updatees = Collections.synchronizedList(new ArrayList<TimeTrackingAware>());

    /**
     * ctor
     *
     * @param updatee       tracking listener
     * @param start         start time stamp
     * @param offsetDisplay offset to be taken into account for display
     */
    public TimeTrackerTask(TimeTrackingAware updatee, long start, long offsetDisplay) {
        addUpdatee(updatee);
        this.start = start;
        this.offset = offsetDisplay;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        long duration = System.currentTimeMillis() - start + offset;
        notifyUpdate(DateUtils.formatElapsedTime(sb, duration / 1000), duration);
    }

    /**
     * adds listener
     *
     * @param updatee the listener to add
     */
    public void addUpdatee(TimeTrackingAware updatee) {
        if (updatee != null) {
            synchronized (updatees) {
                updatees.add(updatee);
            }
        }
    }

    /**
     * removes listener
     *
     * @param listener the listener to remove
     */
    public void removeUpdatee(TimeTrackingAware listener) {
        if (listener != null) {
            synchronized (updatees) {
                updatees.remove(listener);
            }
        }
    }

    /**
     * listener callback
     *
     * @param elapsedTime elapsed time
     */
    private void notifyUpdate(String elapsedTime, long duration) {
        synchronized (updatees) {
            for (TimeTrackingAware updatee : updatees) {
                updatee.onTick(elapsedTime, duration);
            }
        }
    }
}

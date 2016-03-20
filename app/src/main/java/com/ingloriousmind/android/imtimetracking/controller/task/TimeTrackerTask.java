package com.ingloriousmind.android.imtimetracking.controller.task;

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
         * @param duration duration
         */
        void onTick(long duration);
    }

    private long start;
    private long offset;
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
        notifyUpdate(System.currentTimeMillis() - start + offset);
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
     * @param duration elapsed duration
     */
    private void notifyUpdate(long duration) {
        synchronized (updatees) {
            for (TimeTrackingAware updatee : updatees) {
                updatee.onTick(duration);
            }
        }
    }
}

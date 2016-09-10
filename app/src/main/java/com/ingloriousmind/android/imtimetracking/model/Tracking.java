package com.ingloriousmind.android.imtimetracking.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

/**
 * time tracking model
 *
 * @author lavong.soysavanh
 */
@DatabaseTable(tableName = "timetracking")
public class Tracking implements Serializable {

    /**
     * log tag
     */
    public static final String TAG = Tracking.class.getSimpleName();

    /**
     * serialization uid
     */
    private static final long serialVersionUID = 1L;

    /**
     * title
     */
    @DatabaseField(index = true)
    private String title = "unnamed";

    /**
     * description
     */
    @DatabaseField
    private String description;

    /**
     * created
     */
    @DatabaseField(id = true)
    private long created;

    /**
     * last started
     */
    @DatabaseField(index = true)
    private long lastTrackingStarted;

    /**
     * duration
     */
    @DatabaseField
    private long duration;

    /**
     * flag indicating whether tracking is in progress
     */
    @DatabaseField
    private boolean tracking;

    /**
     * temp string buffer
     */
    private transient StringBuffer sb = new StringBuffer();

    /**
     * default ctor
     */
    public Tracking() {
        this.created = System.currentTimeMillis();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        sb.setLength(0);
        sb.append(TAG);
        sb.append(" | title=").append(title);
        sb.append(" | created=").append(created);
        sb.append(" | lastTrackingStarted=").append(lastTrackingStarted);
        sb.append(" | tracking=").append(tracking);
        sb.append(" | duration=").append(duration);
        sb.append(" | desc=").append(description);
        return sb.toString();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public long getLastTrackingStarted() {
        return lastTrackingStarted;
    }

    public void setLastTrackingStarted(long lastTrackingStarted) {
        this.lastTrackingStarted = lastTrackingStarted;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void addDuration(long durationIncrease) {
        this.duration += durationIncrease;
    }

    public boolean isTracking() {
        return tracking;
    }

    public void setTracking(boolean tracking) {
        this.tracking = tracking;
    }
}

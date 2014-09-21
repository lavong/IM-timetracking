package com.ingloriousmind.android.imtimetracking.persistence;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.ingloriousmind.android.imtimetracking.model.Tracking;
import com.ingloriousmind.android.imtimetracking.util.L;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * db helper
 *
 * @author lavong.soysavanh
 */
public class DbHelper extends OrmLiteSqliteOpenHelper {

    /**
     * log tag
     */
    public static final String TAG = DbHelper.class.getSimpleName();

    /**
     * database file name
     */
    private static final String DATABASE_NAME = "trackings.db";

    /**
     * database version
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * singleton instance
     */
    private static DbHelper instance;

    /**
     * ctor
     *
     * @param ctx a context
     */
    private DbHelper(Context ctx) {
        super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * initializes db helper
     *
     * @param ctx a context
     */
    public static void initialize(Context ctx) {
        if (instance == null) {
            L.d(TAG, "initializing database helper instance");
            instance = new DbHelper(ctx);
        }
    }

    /**
     * @return {@link com.ingloriousmind.android.imtimetracking.persistence.DbHelper} instance
     */
    public static DbHelper getInstance() {
        if (instance == null) {
            throw new IllegalStateException("DBHelper uninitialized");
        }
        return instance;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        L.v(TAG, "onCreate");
        try {
            createTables(database, connectionSource);
        } catch (Exception e) {
            L.e(TAG, "unable to create database", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        L.v(TAG, "onUpgrade: " + oldVersion + " -> " + newVersion);
        try {
            // for now, drop and recreate tables
            dropTables(database, connectionSource);
            createTables(database, connectionSource);
        } catch (Exception e) {
            L.e(TAG, "failed upgrading database", e);
        }
    }

    /**
     * drops all tables
     *
     * @param database         the database to operate on
     * @param connectionSource a connection source to operate on
     * @throws Exception
     */
    public void dropTables(SQLiteDatabase database, ConnectionSource connectionSource) throws Exception {
        try {
            TableUtils.dropTable(connectionSource, Tracking.class, true);
        } catch (SQLException e) {
            L.e(TAG, "unable to drop tables", e);
            throw new Exception(e);
        }
    }


    /**
     * creates all tables
     *
     * @param database         the database to operate on
     * @param connectionSource a connection source to operate on
     * @throws Exception
     */
    public void createTables(SQLiteDatabase database, ConnectionSource connectionSource) throws Exception {
        try {
            TableUtils.createTable(connectionSource, Tracking.class);
        } catch (SQLException e) {
            L.e(TAG, "unable to create tables", e);
            throw new Exception(e);
        }
    }


    /**
     * fetches and returns all {@link com.ingloriousmind.android.imtimetracking.model.Tracking} from database.
     *
     * @return the trackings
     */
    public List<Tracking> fetchTrackings() {
        List<Tracking> trackings = new ArrayList<Tracking>();
        try {
            QueryBuilder<Tracking, ?> qb = getDao(Tracking.class).queryBuilder();
            qb.orderBy("lastTrackingStarted", false);
            trackings = qb.query();
        } catch (SQLException e) {
            L.e(TAG, "failed fetching trackings", e);
        }
        return trackings;
    }

    /**
     * fetches and returns the most recent {@link com.ingloriousmind.android.imtimetracking.model.Tracking}
     *
     * @return the most recent tracking
     */
    public Tracking fetchMostRecentTracking() {
        try {
            QueryBuilder<Tracking, ?> qb = getDao(Tracking.class).queryBuilder();
            qb.orderBy("lastTrackingStarted", false).limit(1L);
            List<Tracking> trackings = qb.query();
            if (trackings != null && !trackings.isEmpty())
                return trackings.get(0);
        } catch (SQLException e) {
            L.e(TAG, "failed fetching trackings", e);
        }
        return null;
    }

    /**
     * inserts given tracking into database. or updates an existing one.
     *
     * @param newTracking the tracking to store
     * @return true, if inserted or updated successfully. false, otherwise.
     */
    public boolean storeTracking(Tracking newTracking) {
        try {
            Dao.CreateOrUpdateStatus result = getDao(Tracking.class).createOrUpdate(newTracking);
            return result.isCreated() || result.isUpdated();
        } catch (SQLException e) {
            L.e(TAG, "failed storing tracking", e);
        }
        return false;
    }

    /**
     * removes tracking from database
     *
     * @param tracking the tracking to delete
     * @return true, if removed successfully. false, otherwise.
     */
    public boolean removeTracking(Tracking tracking) {
        try {
            Dao<Tracking, Long> dao = getDao(Tracking.class);
            return dao.deleteById(tracking.getCreated()) > 0;
        } catch (SQLException e) {
            L.e(TAG, "failed removing tracking: " + tracking, e);
        }
        return false;
    }


}

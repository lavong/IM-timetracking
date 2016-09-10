package com.ingloriousmind.android.imtimetracking.time;

import com.ingloriousmind.android.imtimetracking.model.Tracking;
import com.ingloriousmind.android.imtimetracking.persistence.DbHelper;

import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import timber.log.Timber;

/**
 * time tracker
 *
 * @author lavong.soysavanh
 */
public class Tracker {

    private DbHelper dbHelper;
    PublishSubject<Tracking> trackingObservable = PublishSubject.create();
    Subscription timerSubscription;
    Tracking currentTracking;
    private boolean stopOnUnsubscribe;

    public Tracker(DbHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public Tracking resumeIfNecessary() {
        Tracking mostRecentTracking = getMostRecentTracking();
        if (mostRecentTracking != null && mostRecentTracking.isTracking()) {
            return start(mostRecentTracking);
        }
        return null;
    }

    public Tracking start(Tracking tracking) {
        if (tracking == null || timerSubscription != null) {
            return null;
        }
        currentTracking = tracking;
        timerSubscription = Observable.interval(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        long now = System.currentTimeMillis();
                        if (currentTracking.isTracking()) {
                            currentTracking.setDuration(now - currentTracking.getLastTrackingStarted() + currentTracking.getDuration());
                        }
                        currentTracking.setLastTrackingStarted(now);
                        trackingObservable.onNext(currentTracking);
                        Timber.v("started: %s", currentTracking);
                    }
                })
                .doOnUnsubscribe(new Action0() {
                    @Override
                    public void call() {
                        currentTracking.setTracking(!stopOnUnsubscribe);
                        if (stopOnUnsubscribe) {
                            currentTracking.setLastTrackingStarted(System.currentTimeMillis());
                        }
                        persistTracking(currentTracking);
                        timerSubscription = null;
                        Timber.v("stopped: %s", currentTracking);
                    }
                })
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long count) {
                        currentTracking.addDuration(1000);
                        trackingObservable.onNext(currentTracking);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Timber.e(throwable, "timer failed");
                    }
                });

        return currentTracking;
    }

    public Tracking pause() {
        stopOnUnsubscribe = false;
        unsubscribe(timerSubscription);
        return currentTracking;
    }

    public Tracking stop() {
        stopOnUnsubscribe = true;
        unsubscribe(timerSubscription);
        return currentTracking;
    }

    private void unsubscribe(Subscription subscription) {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }

    public Observable<Tracking> observe() {
        return trackingObservable;
    }

    public Tracking getCurrentTracking() {
        return currentTracking;
    }

    private Tracking getMostRecentTracking() {
        Tracking mostRecentTracking = dbHelper.fetchMostRecentTracking();
        Timber.v("read most recent tracking: %s", mostRecentTracking);
        return mostRecentTracking;
    }

    public List<Tracking> getTrackings() {
        return dbHelper.fetchTrackings();
    }

    public void persistTracking(Tracking tracking) {
        dbHelper.storeTracking(tracking);
    }

    public void removeTracking(Tracking tracking) {
        dbHelper.removeTracking(tracking);
    }

}

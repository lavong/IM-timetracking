package com.ingloriousmind.android.imtimetracking.time

import com.ingloriousmind.android.imtimetracking.RxSpecification
import com.ingloriousmind.android.imtimetracking.model.Tracking
import com.ingloriousmind.android.imtimetracking.persistence.DbHelper
import rx.Subscription
import rx.functions.Action1
import spock.util.concurrent.BlockingVariable

import java.util.concurrent.TimeUnit

class TrackerSpec extends RxSpecification {

    DbHelper dbHelper
    Tracker tracker
    Tracking tracking

    def setup() {
        tracking = Mock(Tracking)
        dbHelper = Mock(DbHelper)
        tracker = new Tracker(dbHelper)
    }

    def "should resume"() {
        when:
        tracker.resumeIfNecessary()

        then:
        1 * dbHelper.fetchMostRecentTracking() >> tracking
        1 * tracking.isTracking() >> true

        then:
        1 * tracking.isTracking() >> true
        1 * tracking.setDuration(_)
        1 * tracking.setLastTrackingStarted(_)
    }

    def "should not subscribe when there is no tracking to resume"() {
        when:
        tracker.resumeIfNecessary()

        then:
        1 * dbHelper.fetchMostRecentTracking() >> tracking
        0 * _

        where:
        tracking << [Mock(Tracking), null]
    }

    def "should start"() {
        when:
        tracker.start(tracking)

        then:
        1 * tracking.isTracking() >> true
        1 * tracking.setDuration(_)
        1 * tracking.setLastTrackingStarted(_)
    }

    def "should no-op when no tracking given to start"() {
        when:
        tracker.start(null)

        then:
        0 * _
    }

    def "should not subscribe again when already started"() {
        when:
        tracker.start(tracking)

        then:
        _ * _

        when:
        tracker.start(tracking)

        then:
        0 * _
    }

    def "should tick every second"() {
        given:
        def result = new BlockingVariable<Tracking>(1200, TimeUnit.MILLISECONDS)

        when:
        tracker.start(tracking)

        and:
        tracker.observe().subscribe(new Action1<Tracking>() {
            @Override
            void call(Tracking t) {
                result.set(t)
            }
        })

        then:
        result.get() == tracking
    }

    def "should pause"() {
        given:
        def subscription = Mock(Subscription)
        tracker.timerSubscription = subscription

        when:
        tracker.pause()

        then:
        1 * subscription.unsubscribe()
    }

    def "should stop"() {
        given:
        def subscription = Mock(Subscription)
        tracker.timerSubscription = subscription

        when:
        tracker.stop()

        then:
        1 * subscription.unsubscribe()
    }

    def "should persist tracking on pause/stop"() {
        when:
        def subscription = tracker.observe().subscribe()
        tracker.start(tracking)

        then:
        1 * tracking.setLastTrackingStarted(_)

        when:
        if (finish) tracker.stop()
        else tracker.pause()
        subscription.unsubscribe()

        then:
        tracking.setTracking(!finish)
        (finish ? 1 : 0) * tracking.setLastTrackingStarted(_)
        1 * dbHelper.storeTracking(tracking)

        where:
        finish << [true, false]
    }

    def "should fetch trackings"() {
        when:
        tracker.getTrackings()

        then:
        1 * dbHelper.fetchTrackings()
    }

    def "should delete tracking"() {
        when:
        tracker.removeTracking(tracking)

        then:
        1 * dbHelper.removeTracking(tracking)
    }

    def "should return current tracking"() {
        when:
        tracker.start(tracking)

        then:
        tracker.getCurrentTracking() == tracking
    }

}
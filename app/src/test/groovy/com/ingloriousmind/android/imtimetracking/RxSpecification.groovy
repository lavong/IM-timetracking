package com.ingloriousmind.android.imtimetracking

import rx.Scheduler
import rx.android.plugins.RxAndroidPlugins
import rx.android.plugins.RxAndroidSchedulersHook
import rx.plugins.RxJavaPlugins
import rx.plugins.RxJavaSchedulersHook
import rx.schedulers.Schedulers
import spock.lang.Specification

class RxSpecification extends Specification {

    def setup() {
        def rxPlugins = RxJavaPlugins.getInstance()
        rxPlugins.reset()

        rxPlugins.registerSchedulersHook(new RxJavaSchedulersHook() {
            @Override
            Scheduler getIOScheduler() {
                return Schedulers.immediate()
            }
        })

        def androidRxPlugins = RxAndroidPlugins.getInstance()
        androidRxPlugins.reset()

        androidRxPlugins.registerSchedulersHook(new RxAndroidSchedulersHook() {
            @Override
            Scheduler getMainThreadScheduler() {
                return Schedulers.immediate()
            }
        })
    }
}
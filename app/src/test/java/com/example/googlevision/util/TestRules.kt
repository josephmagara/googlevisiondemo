package com.example.googlevision.util

import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers

/**
 * Created by josephmagara on 2/4/19.
 */


fun setAllThreadsToTrampoline() {
    setRxSchedulersIoOnTrampoline()
    setRxSchedulersMainOnTrampoline()
    setRxSchedulersComputationOnTrampoline()
}

private fun setRxSchedulersMainOnTrampoline() = RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }

private fun setRxSchedulersIoOnTrampoline() = RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }

private fun setRxSchedulersComputationOnTrampoline() = RxJavaPlugins.setComputationSchedulerHandler { Schedulers.trampoline() }
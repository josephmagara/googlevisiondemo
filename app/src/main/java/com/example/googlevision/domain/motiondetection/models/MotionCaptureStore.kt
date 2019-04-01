package com.example.googlevision.domain.motiondetection.models

import io.reactivex.Observable
import io.reactivex.processors.PublishProcessor

class MotionCaptureStore {

    internal var motionPointList: MutableList<MotionPoint> = mutableListOf()

    internal var startTime = 0L
    internal var endTime = 0L

    private var storeIsLocked = false
    private val storeLockPublisher : PublishProcessor<Boolean> = PublishProcessor.create()

    fun addMotionPointToStore(newMotionPoint: MotionPoint) : Boolean{
        if (storeIsLocked) return false

        if (motionPointList.isEmpty()) startStoreInUseTimer()
        motionPointList.add(newMotionPoint)

        return true
    }

    private fun startStoreInUseTimer() {
        startTime = System.currentTimeMillis()
    }

    fun invalidateStore() {
        if (motionPointList.any()) {
            motionPointList.clear()
        }
    }

    fun lockStore() {
        storeIsLocked = true
        storeLockPublisher.onNext(storeIsLocked)
        endTime = System.currentTimeMillis()
    }

    fun unlockStore() {
        storeIsLocked = false
        storeLockPublisher.onNext(storeIsLocked)
    }

    fun storeLockObserver(): Observable<Boolean> = storeLockPublisher.toObservable()
}
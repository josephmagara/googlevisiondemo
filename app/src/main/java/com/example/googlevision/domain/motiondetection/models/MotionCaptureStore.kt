package com.example.googlevision.domain.motiondetection.models

import io.reactivex.Observable
import io.reactivex.processors.PublishProcessor

class MotionCaptureStore {

    internal var motionPointList: MutableList<MotionPoint> = mutableListOf()

    internal var startTime = 0f
    internal var endTime = 0f

    private var storeIsLocked = false
    private val storeLockPublisher : PublishProcessor<Boolean> = PublishProcessor.create()

    fun addMotionPointToStore(newMotionPoint: MotionPoint) : Boolean{
        if (storeIsLocked) return false

        if (motionPointList.isEmpty()) startStoreInUseTimer()
        motionPointList.add(newMotionPoint)

        return true
    }

    private fun startStoreInUseTimer() {
        startTime = System.currentTimeMillis().toFloat()
    }

    fun invalidateStore() {
        if (motionPointList.any()) {
            motionPointList.clear()
            endTime = System.currentTimeMillis().toFloat()
        }
    }

    fun lockStore() {
        storeIsLocked = true
        storeLockPublisher.onNext(storeIsLocked)
    }

    fun unlockStore() {
        storeIsLocked = false
        storeLockPublisher.onNext(storeIsLocked)
    }

    fun storeLockObserver(): Observable<Boolean> = storeLockPublisher.toObservable()
}
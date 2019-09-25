package com.example.googlevision.domain.motiondetection.models

import com.example.googlevision.util.MotionUtil
import io.reactivex.Observable
import io.reactivex.processors.PublishProcessor

class MotionCaptureStore {

    private var motionPointList: MutableList<MotionPoint> = mutableListOf()

    private var startTime = 0L
    private var endTime = 0L

    private var storeIsLocked = false
    private val storeLockPublisher: PublishProcessor<Boolean> = PublishProcessor.create()

    fun addMotionPointToStore(newMotionPoint: MotionPoint): Boolean {
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

    fun getVelocity(): Float {

        val storeCopy = mutableListOf<MotionPoint>().apply {
            addAll(motionPointList)
        }
        return MotionUtil.computeVelocity(
            storeCopy, startTime, endTime
        )

    }
}
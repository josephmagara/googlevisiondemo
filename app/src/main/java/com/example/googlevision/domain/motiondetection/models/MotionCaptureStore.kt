package com.example.googlevision.domain.motiondetection.models

class MotionCaptureStore {

    internal var motionPointList: MutableList<MotionPoint> = mutableListOf()

    private var startTime = 0f
    private var endTime = 0f

    private var storeIsLocked = false

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
    }

    fun unlockStore() {
        storeIsLocked = false
    }

}
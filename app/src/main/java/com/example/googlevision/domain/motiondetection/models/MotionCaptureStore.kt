package com.example.googlevision.domain.motiondetection.models

class MotionCaptureStore {

    internal var motionPointList: MutableList<MotionPoint> = mutableListOf()

    internal var startTime = 0f
    internal var endTime = 0f

    fun addMotionPointToStore(newMotionPoint: MotionPoint) {
        if(motionPointList.isEmpty()) startStoreInUseTimer()
        motionPointList.add(newMotionPoint)
    }

    private fun startStoreInUseTimer() {
        startTime = System.currentTimeMillis().toFloat()
    }

    fun invalidateStore() {
        if (motionPointList.any()) motionPointList.clear() else return
        endTime = System.currentTimeMillis().toFloat()
    }

}
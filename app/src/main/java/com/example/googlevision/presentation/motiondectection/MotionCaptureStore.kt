package com.example.googlevision.presentation.motiondectection

class MotionCaptureStore {

    internal var motionPointList: MutableList<MotionPoint> = mutableListOf()

    fun addMotionPointToStore(newMotionPoint: MotionPoint) = motionPointList.add(newMotionPoint)

    fun invalidateStore() {
        if (motionPointList.any()) motionPointList.clear() else return
    }

    fun flagGradualEvent() =
        addMotionPointToStore(MotionPoint(0f, 0f, 0f))

}
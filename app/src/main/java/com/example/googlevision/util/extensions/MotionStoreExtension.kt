package com.example.googlevision.util.extensions

import com.example.googlevision.presentation.motiondectection.MotionCaptureStore
import com.example.googlevision.presentation.motiondectection.MotionPoint

private val xMotionList: MutableList<Boolean> = mutableListOf()
private val yMotionList: MutableList<Boolean> = mutableListOf()
private val zMotionList: MutableList<Boolean> = mutableListOf()

fun MotionCaptureStore.containsGradualMotionEvent(clearResults: Boolean = true): Boolean {
    if (motionPointList.size < 10) {
        return false
    } else {
        val results = checkForGradualMotion(motionPointList, clearResults)
        return results.any { it }
    }
}

fun MotionCaptureStore.containsStopAfterGradualMotionEvent(): Boolean {
    if (!containsGradualMotionEvent(clearResults = false)) {
        return true
    } else {
        return true
    }
}

private fun checkForGradualMotion(motionPointList: List<MotionPoint>, clearResults: Boolean = true): List<Boolean> {

    motionPointList.forEachIndexed { index, motionPoint ->
        val nextMotionPoint = motionPointList.getOrNull(index)
        nextMotionPoint?.let {
            with(motionPoint.absolueValue()) {
                val nextPointAbsoluteValue = nextMotionPoint.absolueValue()
                com.example.googlevision.util.extensions.xMotionList.add(nextPointAbsoluteValue.xPosition > this.xPosition)
                com.example.googlevision.util.extensions.yMotionList.add(nextPointAbsoluteValue.yPosition > this.yPosition)
                com.example.googlevision.util.extensions.zMotionList.add(nextPointAbsoluteValue.zPosition > this.zPosition)
            }
        }
    }

    val graduallyMovingAlongX = xMotionList.count { it } > xMotionList.count { !it }
    val graduallyMovingAlongY = yMotionList.count { it } > yMotionList.count { !it }
    val graduallyMovingAlongZ = zMotionList.count { it } > zMotionList.count { !it }

    if (clearResults) {
        xMotionList.clear()
        yMotionList.clear()
        zMotionList.clear()
    }

    return listOf(graduallyMovingAlongX, graduallyMovingAlongY, graduallyMovingAlongZ)

}
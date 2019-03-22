package com.example.googlevision.util.extensions

import com.example.googlevision.presentation.motiondectection.MotionCaptureStore
import com.example.googlevision.presentation.motiondectection.MotionPoint

private val xMotionList: MutableList<Boolean> = mutableListOf()
private val yMotionList: MutableList<Boolean> = mutableListOf()
private val zMotionList: MutableList<Boolean> = mutableListOf()

fun MotionCaptureStore.containsGradualMotionEvent(clearResults: Boolean = true): Boolean =
    if (motionPointList.size < 10) {
        false
    } else {
        val results = checkForGradualMotion(motionPointList)
        if (clearResults) {
            clearLists()
        }
        results.any { it }
    }


fun MotionCaptureStore.containsStopAfterGradualMotionEvent(): Boolean {
    if (!containsGradualMotionEvent(clearResults = false)) {
        return true
    } else {
        return true
    }
}

private fun checkForGradualMotion(motionPointList: List<MotionPoint>): List<Boolean> {

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
    return compareMovementsAcrossAxises(xMotionList, yMotionList, zMotionList)
}

private fun compareMovementsAcrossAxises(
    xList: List<Boolean>,
    yList: List<Boolean>,
    zList: List<Boolean>
): List<Boolean> {
    val graduallyMovingAlongX = xList.count { it } > xList.count { !it }
    val graduallyMovingAlongY = yList.count { it } > yList.count { !it }
    val graduallyMovingAlongZ = zList.count { it } > zList.count { !it }

    return listOf(graduallyMovingAlongX, graduallyMovingAlongY, graduallyMovingAlongZ)
}

private fun clearLists() {
    xMotionList.clear()
    yMotionList.clear()
    zMotionList.clear()
}
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
    val containsNoGradualMotion = !containsGradualMotionEvent(clearResults = false)
    if (containsNoGradualMotion) {
        clearLists()
        return true
    } else {

        val results = compareMovementsAcrossAxises(xMotionList, yMotionList, zMotionList)

        val xList = mutableListOf<Float>()
        val yList = mutableListOf<Float>()
        val zList = mutableListOf<Float>()

        motionPointList.forEach { motionPoint ->
            with(motionPoint){
                if(results[0])
                    xList.add(xPosition)
                if(results[1])
                    yList.add(yPosition)
                if(results[2])
                    zList.add(zPosition)
            }
        }

        return true
    }
}

private fun checkForGradualMotion(motionPointList: List<MotionPoint>): List<Boolean> {
    clearLists()
    motionPointList.forEachIndexed { index, motionPoint ->
        val nextMotionPoint = motionPointList.getOrNull(index)
        nextMotionPoint?.let {
            with(motionPoint.absolueValue()) {
                val nextPointAbsoluteValue = nextMotionPoint.absolueValue()
                xMotionList.add(nextPointAbsoluteValue.xPosition > this.xPosition)
                yMotionList.add(nextPointAbsoluteValue.yPosition > this.yPosition)
                zMotionList.add(nextPointAbsoluteValue.zPosition > this.zPosition)
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
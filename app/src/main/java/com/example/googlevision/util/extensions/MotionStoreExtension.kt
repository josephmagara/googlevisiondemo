package com.example.googlevision.util.extensions

import com.example.googlevision.presentation.motiondectection.MotionCaptureStore
import com.example.googlevision.presentation.motiondectection.MotionPoint

private val xMotionList: MutableList<Boolean> = mutableListOf()
private val yMotionList: MutableList<Boolean> = mutableListOf()
private val zMotionList: MutableList<Boolean> = mutableListOf()

fun MotionCaptureStore.containsGradualMotionEvent(): Boolean =
        if (motionPointList.size < 10) {
            false
        } else {
            val results = checkForGradualMotion(motionPointList)
            val containsGradualMotion = results.any{it}

            if(!containsGradualMotion){
                clearLists()
            }
            containsGradualMotion
        }


fun MotionCaptureStore.containsStopAfterGradualMotionEvent(): Boolean {
    if (containsGradualMotionEvent()) {
        val results = compareMovementsAcrossAxises(xMotionList, yMotionList, zMotionList)

        val xList = mutableListOf<Float>()
        val yList = mutableListOf<Float>()
        val zList = mutableListOf<Float>()

        motionPointList.forEach { motionPoint ->
            with(motionPoint) {
                if (results[0])
                    xList.add(xPosition)
                if (results[1])
                    yList.add(yPosition)
                if (results[2])
                    zList.add(zPosition)
            }
        }

        val xMovementHasStopped = if (!xList.any()) {
            true
        } else {
            lastFiveMovesContainStopEvent(xList)
        }

        val yMovementHasStopped = if (!yList.any()) {
            true
        } else {
            lastFiveMovesContainStopEvent(yList)
        }

        val zMovementHasStopped = if (!zList.any()) {
            true
        } else {
            lastFiveMovesContainStopEvent(zList)
        }

        val gradualMotionStopped = xMovementHasStopped && yMovementHasStopped && zMovementHasStopped
        if (gradualMotionStopped) clearLists()

        return gradualMotionStopped
    } else {
        clearLists()
        return true
    }
}

private fun lastFiveMovesContainStopEvent(list: List<Float>): Boolean {
    val lastMovement = list.last()

    val motionStopList = mutableListOf<Boolean>()
    list.takeLast(5).forEach {
        motionStopList.add(lastMovement in it..it.plus(0.075f))
    }

    return motionStopList.count { it } > motionStopList.count { !it }
}

private fun checkForGradualMotion(motionPointList: List<MotionPoint>): List<Boolean> {
    motionPointList.forEachIndexed { index, motionPoint ->
        val nextMotionPoint = motionPointList.getOrNull(index.plus(1))
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
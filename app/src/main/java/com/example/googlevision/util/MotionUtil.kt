package com.example.googlevision.util

import com.example.googlevision.domain.motiondetection.models.MotionPoint
import com.example.googlevision.util.extensions.isGreaterThanSignedComparison


class MotionUtil {

    companion object {


        private val xMotionList: MutableList<Boolean> = mutableListOf()
        private val yMotionList: MutableList<Boolean> = mutableListOf()
        private val zMotionList: MutableList<Boolean> = mutableListOf()

        fun containsGradualMotionEvent(motionPointList: List<MotionPoint>): Boolean =
                if (motionPointList.size < 10) {
                    false
                } else {
                    val results = checkForGradualMotion(motionPointList)
                    val containsGradualMotion = results.any { it }

                    if (!containsGradualMotion) {
                        clearLists()
                    }
                    containsGradualMotion
                }


        fun containsStopAfterGradualMotionEvent(motionPointList: List<MotionPoint>): Boolean {
            if (containsGradualMotionEvent(motionPointList)) {
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
                    lastTenMovesContainStopEvent(xList)
                }

                val yMovementHasStopped = if (!yList.any()) {
                    true
                } else {
                    lastTenMovesContainStopEvent(yList)
                }

                val zMovementHasStopped = if (!zList.any()) {
                    true
                } else {
                    lastTenMovesContainStopEvent(zList)
                }

                val gradualMotionStopped = xMovementHasStopped && yMovementHasStopped && zMovementHasStopped
                if (gradualMotionStopped) clearLists()

                return gradualMotionStopped
            } else {
                clearLists()
                return true
            }
        }

        private fun lastTenMovesContainStopEvent(list: List<Float>): Boolean {
            val lastMovement = list.last()

            val motionStopList = mutableListOf<Boolean>()
            list.takeLast(10).forEach {
                motionStopList.add(lastMovement in it..it.plus(0.065f))
            }

            return motionStopList.count { it } > motionStopList.count { !it }
        }

        private fun checkForGradualMotion(motionPointList: List<MotionPoint>): List<Boolean> {
            motionPointList.forEachIndexed { index, motionPoint ->
                val nextMotionPoint = motionPointList.getOrNull(index.plus(1))
                nextMotionPoint?.let {
                    xMotionList.add(nextMotionPoint.xPosition.isGreaterThanSignedComparison(motionPoint.xPosition))
                    yMotionList.add(nextMotionPoint.yPosition.isGreaterThanSignedComparison(motionPoint.yPosition))
                    zMotionList.add(nextMotionPoint.zPosition.isGreaterThanSignedComparison(motionPoint.zPosition))
                }
            }

            return compareMovementsAcrossAxises(xMotionList, yMotionList, zMotionList)
        }

        private fun compareMovementsAcrossAxises(xList: List<Boolean>,
                                                 yList: List<Boolean>,
                                                 zList: List<Boolean>): List<Boolean> {

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
    }
}
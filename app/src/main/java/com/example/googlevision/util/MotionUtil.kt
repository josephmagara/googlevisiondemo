package com.example.googlevision.util

import com.example.googlevision.domain.motiondetection.models.MotionPoint
import kotlin.math.absoluteValue


class MotionUtil {

    companion object {

        fun computeVelocity(list: List<MotionPoint>, startTime: Long, endTime: Long): Float {
            val time = endTime - startTime
            var xDistance = 0f
            var yDistance = 0f
            var zDistance = 0f
            list.forEach {
                xDistance = +it.xPosition.absoluteValue
                yDistance = +it.yPosition.absoluteValue
                zDistance = +it.zPosition.absoluteValue
            }

            val distance = maxOf(xDistance, yDistance, zDistance)
            return distance / time.div(1000.000).toFloat()
        }
    }

}
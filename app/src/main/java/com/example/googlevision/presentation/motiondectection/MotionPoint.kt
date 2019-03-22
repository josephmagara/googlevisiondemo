package com.example.googlevision.presentation.motiondectection

import kotlin.math.absoluteValue

data class MotionPoint(val xPosition: Float, val yPosition: Float, val zPosition: Float) {

    fun absolueValue(): MotionPoint =
        MotionPoint(this.xPosition.absoluteValue, this.yPosition.absoluteValue, this.zPosition.absoluteValue)
}
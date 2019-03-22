package com.example.googlevision.presentation.motiondectection

import com.example.googlevision.util.extensions.containsGradualMotionEvent
import io.reactivex.Observable
import io.reactivex.processors.PublishProcessor
import javax.inject.Singleton

/**
 * Created by josephmagara on 21/3/19.
 */
@Singleton
class MotionCaptor {
    private var previousXPosition: Float = 0f
    private var previousYPosition: Float = 0f
    private var previousZPosition: Float = 0f

    private val significantPauseOccurredPublisher: PublishProcessor<Boolean> = PublishProcessor.create()
    private val motionCaptureStore = MotionCaptureStore()

    private fun computeNewMotion(newXPosition: Float, newYPosition: Float, newZPosition: Float) {
        if (newXPosition in previousXPosition.minus(0.5f)..previousXPosition.plus(0.5f) &&
            newYPosition in previousYPosition.minus(0.5f)..previousYPosition.plus(0.5f) &&
            newZPosition in previousZPosition.minus(0.5f)..previousZPosition.plus(0.5f)
        ) {
            updateMotionCaptureStore(newXPosition, newYPosition, newZPosition)
            if(!isGraduallyMoving()) {
                significantPauseOccurredPublisher.onNext(true)
            }
        } else {
            motionCaptureStore.invalidateStore()
            significantPauseOccurredPublisher.onNext(false)
        }

        setNewCoordinates(newXPosition, newYPosition, newZPosition)
    }

    private fun isGraduallyMoving(): Boolean = motionCaptureStore.containsGradualMotionEvent()

    private fun updateMotionCaptureStore(newXPosition: Float, newYPosition: Float, newZPosition: Float) =
        motionCaptureStore.addMotionPointToStore(MotionPoint(newXPosition, newYPosition, newZPosition))


    private fun setNewCoordinates(newXPosition: Float, newYPosition: Float, newZPosition: Float) {
        previousXPosition = newXPosition
        previousYPosition = newYPosition
        previousZPosition = newZPosition
    }

    private fun hasNotBeenInitialized(): Boolean =
        arrayOf(previousXPosition, previousYPosition, previousZPosition).all { it == 0f }

    fun significantPauseOccurred(): Observable<Boolean> = significantPauseOccurredPublisher.toObservable()

    fun captureMotion(newXPosition: Float, newYPosition: Float, newZPosition: Float) {

        if (hasNotBeenInitialized()) {
            setNewCoordinates(newXPosition, newYPosition, newZPosition)
            significantPauseOccurredPublisher.onNext(false)
        } else {
            computeNewMotion(newXPosition, newYPosition, newZPosition)
        }
    }

}
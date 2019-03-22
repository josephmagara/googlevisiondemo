package com.example.googlevision.presentation.motiondectection

import android.app.Activity
import android.widget.Toast
import com.example.googlevision.util.MotionUtil
import io.reactivex.Observable
import io.reactivex.processors.PublishProcessor
import javax.inject.Singleton

/**
 * Created by josephmagara on 21/3/19.
 */
@Singleton
class MotionCaptor(private val activity: Activity) {

    private var previousXPosition: Float = 0f
    private var previousYPosition: Float = 0f
    private var previousZPosition: Float = 0f

    private val significantPauseOccurredPublisher: PublishProcessor<Boolean> = PublishProcessor.create()
    private val motionCaptureStore = MotionCaptureStore()

    private fun computeNewMotion(newXPosition: Float, newYPosition: Float, newZPosition: Float) {
        var message = "we "

        if (newXPosition in previousXPosition.minus(0.3f)..previousXPosition.plus(0.3f) &&
                newYPosition in previousYPosition.minus(0.3f)..previousYPosition.plus(0.3f) &&
                newZPosition in previousZPosition.minus(0.3f)..previousZPosition.plus(0.3f)
        ) {
            updateMotionCaptureStore(newXPosition, newYPosition, newZPosition)

            val gradualMotionOccurring = isGraduallyMoving()
            val finishedGraduallyMoving = gradualMotionOccurring && hasStoppedGraduallyMoving()

            when {
                finishedGraduallyMoving -> {
                    message += "have stopped"
                    significantPauseOccurredPublisher.onNext(true)
                }
                gradualMotionOccurring -> {
                    message += "are gradually moving"
                    significantPauseOccurredPublisher.onNext(false)
                }
                else -> {
                    message += "are still enough for photo"
                    significantPauseOccurredPublisher.onNext(true)
                }
            }
        } else {
            message += "are moving fast"
            significantPauseOccurredPublisher.onNext(false)
        }

        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
        setNewCoordinates(newXPosition, newYPosition, newZPosition)
    }

    private fun isGraduallyMoving(): Boolean =
            MotionUtil.containsGradualMotionEvent(motionCaptureStore.motionPointList)

    private fun hasStoppedGraduallyMoving(): Boolean =
            MotionUtil.containsStopAfterGradualMotionEvent(motionCaptureStore.motionPointList)

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
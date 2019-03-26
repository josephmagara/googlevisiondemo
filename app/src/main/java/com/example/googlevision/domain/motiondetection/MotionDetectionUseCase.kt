package com.example.googlevision.domain.motiondetection

import com.example.googlevision.domain.motiondetection.models.MotionCaptureStore
import com.example.googlevision.domain.motiondetection.models.MotionPoint
import com.example.googlevision.util.MotionUtil
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.disposables.Disposables
import io.reactivex.processors.PublishProcessor
import io.reactivex.subjects.PublishSubject
import timber.log.Timber

/**
 * Created by josephmagara on 25/3/19.
 */
class MotionDetectionUseCase {

    private var previousXPosition: Float = 0f
    private var previousYPosition: Float = 0f
    private var previousZPosition: Float = 0f

    private val significantPauseOccurredPublisher: PublishProcessor<Boolean> = PublishProcessor.create()
    private val motionCaptureStore = MotionCaptureStore()

    private val motionEventCountPublisher: PublishSubject<Int> = PublishSubject.create()

    private var motionEventObserver: Disposable = Disposables.disposed()

    init {
        motionEventObserver = motionEventCountPublisher
            .skipWhile { it % 30 == 0 } //Take online 30 at a time
            .subscribe {
               // computeNewMotion()
            }
    }

    private fun computeNewMotion(newXPosition: Float, newYPosition: Float, newZPosition: Float) {

        if (newXPosition in previousXPosition.minus(0.3f)..previousXPosition.plus(0.3f) &&
            newYPosition in previousYPosition.minus(0.3f)..previousYPosition.plus(0.3f) &&
            newZPosition in previousZPosition.minus(0.3f)..previousZPosition.plus(0.3f)
        ) {
            updateMotionCaptureStore(newXPosition, newYPosition, newZPosition)

            val gradualMotionOccurring = isGraduallyMoving()
            val finishedGraduallyMoving = gradualMotionOccurring && hasStoppedGraduallyMoving()

            when {
                finishedGraduallyMoving -> {
                    significantPauseOccurredPublisher.onNext(true)
                }
                gradualMotionOccurring -> {
                    significantPauseOccurredPublisher.onNext(false)
                }
                else -> {
                    significantPauseOccurredPublisher.onNext(true)
                }

            }
            significantPauseOccurredPublisher.onNext(true)
        } else {
            Timber.d("We're moving")
            significantPauseOccurredPublisher.onNext(false)
        }
        setNewCoordinates(newXPosition, newYPosition, newZPosition)
    }

    private fun isGraduallyMoving(): Boolean =
        MotionUtil.containsGradualMotionEvent(motionCaptureStore.motionPointList)

    private fun hasStoppedGraduallyMoving(): Boolean =
        MotionUtil.containsStopAfterGradualMotionEvent(motionCaptureStore.motionPointList)

    private fun updateMotionCaptureStore(newXPosition: Float, newYPosition: Float, newZPosition: Float) {
        motionCaptureStore.addMotionPointToStore(
            MotionPoint(
                newXPosition,
                newYPosition,
                newZPosition
            )
        )
        motionEventCountPublisher.onNext(1)
    }

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
package com.example.googlevision.domain.motiondetection

import com.example.googlevision.domain.motiondetection.models.MotionCaptureStore
import com.example.googlevision.domain.motiondetection.models.MotionPoint
import com.example.googlevision.util.MotionUtil
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.disposables.Disposables
import io.reactivex.processors.PublishProcessor
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import java.util.concurrent.TimeUnit

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
    private val motionSnapshotCaptureTrigger: PublishSubject<Any> = PublishSubject.create()

    private var motionSnapshotCaptureTimerObservable: Disposable = Disposables.disposed()
    private var motionEventCountObserver: Disposable = Disposables.disposed()


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
        val storeUpdated = motionCaptureStore.addMotionPointToStore(
            MotionPoint(
                newXPosition,
                newYPosition,
                newZPosition
            )
        )
        if (storeUpdated) motionEventCountPublisher.onNext(1)
    }

    private fun triggerMotionSnapshotCapture() {
        //Clear the disposables that call this method
        motionEventCountObserver.dispose()
        motionSnapshotCaptureTimerObservable.dispose()

        //Reset the previous coordinates
        previousXPosition = 0f
        previousYPosition = 0f
        previousZPosition = 0f

        //Capture the snapshot of the store and start the calculation process
        motionSnapshotCaptureTrigger.onNext(true)
    }


    private fun startMotionSnapshotCaptureTimer() {
        motionSnapshotCaptureTimerObservable = Completable.timer(2000L, TimeUnit.MILLISECONDS)
            .subscribe {
                motionCaptureStore.lockStore()
                triggerMotionSnapshotCapture()
            }
    }

    private fun setUpMotionEventCountObserver(){
        motionEventCountObserver = motionEventCountPublisher
            .skip(50)
            .subscribe {
                triggerMotionSnapshotCapture()
            }
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

            //Unlock the store so that we can capture the motion data
            motionCaptureStore.unlockStore()

            //Start the timer and counter that will trigger the snapshot of the motion store (which ever fires first
            // will trigger the snapshot capture and then reset both itself and the other timer/counter)
            startMotionSnapshotCaptureTimer()
            setUpMotionEventCountObserver()
        } else {
            computeNewMotion(newXPosition, newYPosition, newZPosition)
        }
    }
}
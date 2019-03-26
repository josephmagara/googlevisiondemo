package com.example.googlevision.domain.motiondetection

import com.example.googlevision.domain.motiondetection.models.MotionCaptureStore
import com.example.googlevision.domain.motiondetection.models.MotionPoint
import com.example.googlevision.util.MotionUtil
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.disposables.Disposables
import io.reactivex.processors.PublishProcessor
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.concurrent.TimeUnit

/**
 * Created by josephmagara on 25/3/19.
 */
class MotionDetectionUseCase {

    private var previousXPosition: Float = 0f
    private var previousYPosition: Float = 0f
    private var previousZPosition: Float = 0f
    private var isComputingMotion: Boolean = false

    private val motionCaptureStore = MotionCaptureStore()

    private val motionEventCountPublisher: PublishProcessor<Int> = PublishProcessor.create()
    private val motionSnapshotCaptureTrigger: PublishProcessor<Any> = PublishProcessor.create()
    private val significantPauseOccurredPublisher: PublishProcessor<Boolean> = PublishProcessor.create()

    private var motionEventCountObserver: Disposable = Disposables.disposed()
    private var isComputingMotionObserver: Disposable = Disposables.disposed()
    private var motionSnapshotTriggerObserver: Disposable = Disposables.disposed()
    private var motionSnapshotCaptureTimerObservable: Disposable = Disposables.disposed()

    init {
        setUp()
    }

    private fun computeNewMotion(newXPosition: Float, newYPosition: Float, newZPosition: Float) {

        if (newXPosition in previousXPosition.minus(0.3f)..previousXPosition.plus(0.3f) &&
            newYPosition in previousYPosition.minus(0.3f)..previousYPosition.plus(0.3f) &&
            newZPosition in previousZPosition.minus(0.3f)..previousZPosition.plus(0.3f)
        ) {
            updateMotionCaptureStore(newXPosition, newYPosition, newZPosition)
            /*
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
            */
        } else {
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

                // Lock the store so that no more motion events are added to it.
                motionCaptureStore.lockStore()
                triggerMotionSnapshotCapture()
            }
    }

    private fun setUpMotionEventCountObserver() {
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



    fun setUp() {
        motionSnapshotTriggerObserver = motionSnapshotCaptureTrigger.observeOn(Schedulers.computation())
            .subscribeOn(Schedulers.computation())
            .subscribe {
                val velocity = MotionUtil.computeVelocity(
                    motionCaptureStore.motionPointList.toList(),
                    motionCaptureStore.startTime,
                    motionCaptureStore.endTime
                )

                if (velocity > 200f){
                    Timber.d("We're moving")
                    significantPauseOccurredPublisher.onNext(false)
                }else{
                    Timber.d("We're moving")
                    significantPauseOccurredPublisher.onNext(true)
                }

                //Unlock the store so that we can capture the motion data
                motionCaptureStore.unlockStore()
            }

        isComputingMotionObserver = motionCaptureStore.storeLockObserver().subscribe { isComputingMotion = it }
    }

    fun significantPauseOccurred(): Observable<Boolean> = significantPauseOccurredPublisher.toObservable()

    fun captureMotion(newXPosition: Float, newYPosition: Float, newZPosition: Float) {

        if (isComputingMotion) return

        if (hasNotBeenInitialized()) {
            setNewCoordinates(newXPosition, newYPosition, newZPosition)
            significantPauseOccurredPublisher.onNext(false)

            //Start the timer and counter that will trigger the snapshot of the motion store (which ever fires first
            // will trigger the snapshot capture and then reset both itself and the other timer/counter)
            startMotionSnapshotCaptureTimer()
            setUpMotionEventCountObserver()
        } else {
            computeNewMotion(newXPosition, newYPosition, newZPosition)
        }
    }

    fun onCleared(){
        motionEventCountObserver.dispose()
        isComputingMotionObserver.dispose()
        motionSnapshotTriggerObserver.dispose()
        motionSnapshotCaptureTimerObservable.dispose()
    }
}
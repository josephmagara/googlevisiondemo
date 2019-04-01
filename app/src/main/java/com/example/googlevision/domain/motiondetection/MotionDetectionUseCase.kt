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

    private fun captureNewMotion(newXPosition: Float, newYPosition: Float, newZPosition: Float) {
        updateMotionCaptureStore(newXPosition, newYPosition, newZPosition)
        setNewCoordinates(newXPosition, newYPosition, newZPosition)
    }

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

    private fun setNewCoordinates(newXPosition: Float, newYPosition: Float, newZPosition: Float) {
        previousXPosition = newXPosition
        previousYPosition = newYPosition
        previousZPosition = newZPosition
    }

    private fun triggerMotionSnapshotCapture() {
        motionCaptureStore.lockStore()

        //Reset the previous coordinates
        previousXPosition = 0f
        previousYPosition = 0f
        previousZPosition = 0f

        //Capture the snapshot of the store and start the calculation process
        motionSnapshotCaptureTrigger.onNext(true)
    }


    private fun setUpMotionSnapshotCaptureTimer() {
        motionSnapshotCaptureTimerObservable = Completable.timer(2000L, TimeUnit.MILLISECONDS)
            .subscribe {

                // Lock the store so that no more motion events are added to it.
                triggerMotionSnapshotCapture()
            }
    }

    private fun setUpMotionEventCountObserver() {
        motionEventCountObserver = motionEventCountPublisher
            .buffer(30)
            .subscribe {
                triggerMotionSnapshotCapture()
            }
    }

    private fun hasNotBeenInitialized(): Boolean =
        arrayOf(previousXPosition, previousYPosition, previousZPosition).all { it == 0f }


    fun setUp() {
        motionSnapshotTriggerObserver = motionSnapshotCaptureTrigger
            .observeOn(Schedulers.computation())
            .subscribeOn(Schedulers.computation())
            .subscribe {

                // We create a copy of the store so that when we invalidate it later, we won't get into a situation
                // were the variable that is being iterated over (when computing velocity) gets cleared thus resulting in
                // a null-pointer exception
                val storeCopy = mutableListOf<MotionPoint>().apply {
                    addAll(motionCaptureStore.motionPointList)
                }
                val velocity = MotionUtil.computeVelocity(
                    storeCopy, motionCaptureStore.startTime, motionCaptureStore.endTime
                )

                if (velocity > 0.7f) {
                    Timber.d("We're moving: Velocity: $velocity")
                    significantPauseOccurredPublisher.onNext(false)
                } else {
                    Timber.d("We're still: Velocity: $velocity")
                    significantPauseOccurredPublisher.onNext(true)
                }

                motionCaptureStore.invalidateStore()
                //Unlock the store so that we can capture the motion data
                motionCaptureStore.unlockStore()
            }

        isComputingMotionObserver = motionCaptureStore.storeLockObserver().subscribe {
            // Timber.d("Store is locked: $it")
            isComputingMotion = it
        }
    }

    fun significantPauseOccurred(): Observable<Boolean> = significantPauseOccurredPublisher.toObservable()

    fun captureMotion(newXPosition: Float, newYPosition: Float, newZPosition: Float) {

        if (isComputingMotion) return


        //Start the timer and counter that will trigger the snapshot of the motion store (which ever fires first
        // will trigger the snapshot capture and then reset both itself and the other timer/counter)
        //if (motionSnapshotCaptureTimerObservable.isDisposed) setUpMotionSnapshotCaptureTimer()
        if (motionEventCountObserver.isDisposed) setUpMotionEventCountObserver()


        if (hasNotBeenInitialized()) {
            setNewCoordinates(newXPosition, newYPosition, newZPosition)
            significantPauseOccurredPublisher.onNext(false)

        } else {
            captureNewMotion(newXPosition, newYPosition, newZPosition)
        }
    }

    fun onCleared() {
        motionEventCountObserver.dispose()
        isComputingMotionObserver.dispose()
        motionSnapshotTriggerObserver.dispose()
        motionSnapshotCaptureTimerObservable.dispose()
    }
}
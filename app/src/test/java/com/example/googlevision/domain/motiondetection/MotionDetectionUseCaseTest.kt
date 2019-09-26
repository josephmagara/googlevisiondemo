package com.example.googlevision.domain.motiondetection

import com.example.googlevision.domain.motiondetection.models.MotionCaptureStore
import com.example.googlevision.util.setAllThreadsToTrampoline
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Observable
import io.reactivex.processors.PublishProcessor
import org.jetbrains.spek.api.dsl.context
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.jetbrains.spek.subject.SubjectSpek
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import java.util.concurrent.ThreadLocalRandom
import kotlin.test.assertEquals

/**
 * Created by josephmagara on 2/4/19.
 */
@RunWith(JUnitPlatform::class)
class MotionDetectionUseCaseTest : SubjectSpek<MotionDetectionUseCase>({

    setAllThreadsToTrampoline()

    val mockMotionCaptureStore : MotionCaptureStore = mock()
    val lockStorePublisher : PublishProcessor<Boolean> = PublishProcessor.create()
    subject {

        whenever(mockMotionCaptureStore.getVelocity()).thenReturn(0.4f)
        whenever(mockMotionCaptureStore.unlockStore())
        whenever(mockMotionCaptureStore.storeLockObserver()).thenReturn(Observable.just(false))

        MotionDetectionUseCase(mockMotionCaptureStore)
    }

    context("Motion detection required"){
        given("motion events occurred"){
            on("30 motion detection events fired"){
                lockStorePublisher.onNext(false)
                mockMotionCaptureStore.unlockStore()
                val testFocus = subject.significantPauseOccurred().test()
                val lowerBound = 0.0
                val upperBound = 0.6

                val randomGenerator = ThreadLocalRandom.current()
                repeat(15) {
                    val randomX = randomGenerator.nextDouble(lowerBound, upperBound).toFloat()
                    val randomY = randomGenerator.nextDouble(lowerBound, upperBound).toFloat()
                    val randomZ = randomGenerator.nextDouble(lowerBound, upperBound).toFloat()
                        subject.captureMotion(randomX, randomY, randomZ)
                }

                it("will compute whether the phone is still or not "){
                    assertEquals(true, testFocus.values().last())
                }
            }
        }
    }
})
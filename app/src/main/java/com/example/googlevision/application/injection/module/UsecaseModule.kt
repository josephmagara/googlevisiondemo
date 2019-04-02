package com.example.googlevision.application.injection.module

import com.example.googlevision.domain.motiondetection.MotionDetectionUseCase
import com.example.googlevision.domain.motiondetection.models.MotionCaptureStore
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by josephmagara on 2/4/19.
 */

@Module
class UsecaseModule {

    @Singleton
    @Provides
    fun providesMotionDetectionUsecase(motionCaptureStore: MotionCaptureStore): MotionDetectionUseCase =
        MotionDetectionUseCase(motionCaptureStore)
}
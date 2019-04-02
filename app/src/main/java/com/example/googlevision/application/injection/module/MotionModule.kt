package com.example.googlevision.application.injection.module

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import com.example.googlevision.data.motiondectection.MotionDetector
import com.example.googlevision.domain.motiondetection.MotionDetectionUseCase
import com.example.googlevision.domain.motiondetection.models.MotionCaptureStore
import dagger.Module
import dagger.Provides

/**
 * Created by josephmagara on 25/3/19.
 */

@Module
class MotionModule {

    @Provides
    fun providesSensorManager(context: Context): SensorManager =
            context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    @Provides
    fun providesAccelerometer(sensorManager: SensorManager): Sensor =
            sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    @Provides
    fun providesMotionDetector(sensorManager: SensorManager,
                               accelerometer: Sensor,
                               motionDetectionUseCase: MotionDetectionUseCase): MotionDetector =
            MotionDetector(sensorManager, accelerometer, motionDetectionUseCase)


    @Provides
    fun providesMotionCaptureStore(): MotionCaptureStore = MotionCaptureStore()
}
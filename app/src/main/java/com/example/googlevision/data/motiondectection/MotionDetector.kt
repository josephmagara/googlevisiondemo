package com.example.googlevision.data.motiondectection

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.example.googlevision.domain.motiondetection.MotionDetectionUseCase
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposables
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Created by josephmagara on 20/3/19.
 */
class MotionDetector @Inject constructor(
        private val sensorManager: SensorManager,
        private val accelerometer: Sensor) : SensorEventListener {

    private val motionDetectionUseCase = MotionDetectionUseCase()

    private var delayTimerDisposable = Disposables.disposed()
    private var significantMotionObserver = Disposables.disposed()
    private val gravity = arrayOf(0.0f, 0.0f, 0.0f, 0.0f, 0.0f)
    private var eventsCapturedCounter = 0

    var deviceIsStill: Boolean = false
        set(value) {
            Timber.d("We are moving: $value")
            if (value) {
                if (delayTimerDisposable.isDisposed) {
                    delayTimerDisposable = Completable.timer(250L, TimeUnit.MILLISECONDS)
                            .observeOn(Schedulers.computation())
                            .subscribeOn(Schedulers.computation())
                            .subscribe {
                                field = value
                            }
                }
            } else {
                delayTimerDisposable.dispose()
                field = value
            }
        }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let { sensorEvent ->
            if (sensorEvent.sensor.type == Sensor.TYPE_ACCELEROMETER) {

                // Shake detection
                val alpha = 0.8f

                // Isolate the force of gravity with the low-pass filter.
                gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0]
                gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1]
                gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2]

                // Remove the gravity contribution with the high-pass filter.
                val x = event.values[0] - gravity[0]
                val y  = event.values[1] - gravity[1]
                val z = event.values[2] - gravity[2]

                eventsCapturedCounter++
                if (eventsCapturedCounter < 6) return

                motionDetectionUseCase.captureMotion(x, y, z)
            }
        }
    }

    fun invalidateDeviceIsStillFlag() {
        deviceIsStill = false
    }

    fun registerListener() {
        motionDetectionUseCase.setUp()
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI)
        significantMotionObserver = motionDetectionUseCase.significantPauseOccurred()
                .distinctUntilChanged()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.computation())
                .subscribe { significantPauseOccurred ->
                    deviceIsStill = significantPauseOccurred
                }
    }

    fun unRegisterListener() {
        motionDetectionUseCase.onCleared()
        sensorManager.unregisterListener(this)
        significantMotionObserver.dispose()
    }
}
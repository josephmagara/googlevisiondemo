package com.example.googlevision.data.motiondectection

import android.app.Activity
import android.content.Context.SENSOR_SERVICE
import android.hardware.Sensor
import android.hardware.Sensor.TYPE_ACCELEROMETER
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

/**
 * Created by josephmagara on 20/3/19.
 */
class MotionDetector(activity: Activity) : SensorEventListener {

    private val sensorManager: SensorManager = activity.getSystemService(SENSOR_SERVICE) as SensorManager
    private val accelerometer: Sensor = sensorManager.getDefaultSensor(TYPE_ACCELEROMETER)
    private val motionDetectionUseCase = MotionDetectionUseCase()

    private var delayTimerDisposable = Disposables.disposed()
    private var significantMotionObserver = Disposables.disposed()

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
                val gravity = event.values.clone()

                // Shake detection
                val x = gravity[0]
                val y = gravity[1]
                val z = gravity[2]

                motionDetectionUseCase.captureMotion(x, y, z)
            }
        }
    }

    fun invalidateDeviceIsStillFlag() { deviceIsStill = false }

    fun registerListener() {
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
        sensorManager.unregisterListener(this)
        significantMotionObserver.dispose()
    }
}
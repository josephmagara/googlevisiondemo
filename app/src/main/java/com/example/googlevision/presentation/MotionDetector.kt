package com.example.googlevision.presentation

import android.app.Activity
import android.content.Context.SENSOR_SERVICE
import android.hardware.Sensor
import android.hardware.Sensor.TYPE_ACCELEROMETER
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
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
    private var delayTimerDisposable = Disposables.disposed()

    private var gravity = floatArrayOf()
    private var acceleration = 0.00f
    private var currentAcceleration = SensorManager.GRAVITY_EARTH
    private var lastAcceleration = SensorManager.GRAVITY_EARTH

    var deviceIsStill: Boolean = false
        set(value) {
            Timber.d("We are moving: $value")
            if (value) {

                if (delayTimerDisposable.isDisposed) {
                    delayTimerDisposable = Completable.timer(1000L, TimeUnit.MILLISECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
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
                gravity = event.values.clone()

                // Shake detection
                val x = gravity[0]
                val y = gravity[1]
                val z = gravity[2]

                val valueToCompute = x * x + y * y + z * z
                currentAcceleration = Math.sqrt(valueToCompute.toDouble()).toFloat()

                val delta = currentAcceleration - lastAcceleration
                acceleration = acceleration * 0.9f + delta

                // Make the value at the end higher or lower according to how much motion you want to detect
                deviceIsStill = acceleration <= 0.3
            }
        }
    }

    fun registerListener() {
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI)
    }

    fun unRegisterListener() {
        sensorManager.unregisterListener(this)
    }
}
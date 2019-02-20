package com.example.googlevision.util.extensions

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context.CAMERA_SERVICE
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Build
import android.util.Log
import android.view.Surface
import androidx.annotation.RequiresApi
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata


/**
 * Created by josephmagara on 20/2/19.
 */

private val ORIENTATIONS = intArrayOf(
    Surface.ROTATION_0, 90,
    Surface.ROTATION_90, 0,
    Surface.ROTATION_180, 270,
    Surface.ROTATION_270, 180)

/**
 * Get the angle by which an image must be rotated given the device's current
 * orientation.
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
@Throws(CameraAccessException::class)
private fun Activity.getRotationCompensation(cameraId: String): Int {
    // Get the device's current rotation relative to its "native" orientation.
    // Then, from the ORIENTATIONS table, look up the angle the image must be
    // rotated to compensate for the device's rotation.
    val deviceRotation = windowManager.defaultDisplay.rotation
    var rotationCompensation = ORIENTATIONS.get(deviceRotation)

    // On most devices, the sensor orientation is 90 degrees, but for some
    // devices it is 270 degrees. For devices with a sensor orientation of
    // 270, rotate the image an additional 180 ((270 + 270) % 360) degrees.
    val cameraManager = getSystemService(CAMERA_SERVICE) as CameraManager
    val sensorOrientation = cameraManager
        .getCameraCharacteristics(cameraId)
        .get(CameraCharacteristics.SENSOR_ORIENTATION)!!
    rotationCompensation = (rotationCompensation + sensorOrientation + 270) % 360

    // Return the corresponding FirebaseVisionImageMetadata rotation value.
    val result: Int
    when (rotationCompensation) {
        0 -> result = FirebaseVisionImageMetadata.ROTATION_0
        90 -> result = FirebaseVisionImageMetadata.ROTATION_90
        180 -> result = FirebaseVisionImageMetadata.ROTATION_180
        270 -> result = FirebaseVisionImageMetadata.ROTATION_270
        else -> {
            result = FirebaseVisionImageMetadata.ROTATION_0
            Log.e(TAG, "Bad rotation value: $rotationCompensation")
        }
    }
    return result
}
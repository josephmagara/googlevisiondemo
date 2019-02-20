package com.example.googlevision.util.extensions

import android.Manifest
import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Context.CAMERA_SERVICE
import android.content.pm.PackageManager
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Build
import android.os.Environment
import android.util.Log
import android.view.Surface
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.googlevision.util.CAMERA_PERMISSION_REQUEST_CODE
import com.example.googlevision.util.DATE_FORMAT
import com.example.googlevision.util.READ_STORAGE_PERMISSION_REQUEST_CODE
import com.example.googlevision.util.WRITE_TO_STORAGE_PERMISSION_REQUEST_CODE
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by josephmagara on 20/2/19.
 */

private val ORIENTATIONS = intArrayOf(
    Surface.ROTATION_0, 90,
    Surface.ROTATION_90, 0,
    Surface.ROTATION_180, 270,
    Surface.ROTATION_270, 180
)

/**
 * Get the angle by which an image must be rotated given the device's current
 * orientation.
 */


val permissionList = mapOf(
    Manifest.permission.CAMERA to CAMERA_PERMISSION_REQUEST_CODE,
    Manifest.permission.READ_EXTERNAL_STORAGE to READ_STORAGE_PERMISSION_REQUEST_CODE,
    Manifest.permission.WRITE_EXTERNAL_STORAGE to WRITE_TO_STORAGE_PERMISSION_REQUEST_CODE
)

fun Activity.hasAllNeededPermissions(permissions: Map<String, Int> = permissionList): Boolean =
    permissions.keys.all { !permissionDenied(this, it) }

fun Activity.requestPermissions(
    permissions: Map<String, Int> = permissionList
): Boolean {

    var requestCode = 1

    val nonGrantedPermissionValues = permissions
        .filter { permission ->
            permissionDenied(this, permission.key)
                .also { isNotGranted ->
                    if (isNotGranted) {
                        requestCode *= permission.value
                    }
                }
        }.map { it.key }.toTypedArray()

    if (nonGrantedPermissionValues.isNotEmpty()) {
        ActivityCompat.requestPermissions(this, nonGrantedPermissionValues, requestCode)
    }

    return nonGrantedPermissionValues.isNotEmpty()
}

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
@Throws(CameraAccessException::class)
fun Activity.getRotationCompensation(cameraId: String): Int {
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

fun Activity.createFile(fileName: String): File {
    val timeStamp: String = SimpleDateFormat(DATE_FORMAT).format(Date())
    val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile(
        "JPEG_${fileName}_$timeStamp", /* prefix */
        ".jpg", /* suffix */
        storageDir /* directory */
    )
}


private fun permissionDenied(context: Context, permission: String) =
    ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_DENIED

package com.example.googlevision.util.extensions

import android.Manifest.permission.*
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.googlevision.util.CAMERA_PERMISSION_REQUEST_CODE
import com.example.googlevision.util.READ_STORAGE_PERMISSION_REQUEST_CODE
import com.example.googlevision.util.WRITE_TO_STORAGE_PERMISSION_REQUEST_CODE

/**
 * Created by josephmagara on 19/2/19.
 */

val permissionList = mapOf(
    CAMERA to CAMERA_PERMISSION_REQUEST_CODE,
    READ_EXTERNAL_STORAGE to READ_STORAGE_PERMISSION_REQUEST_CODE,
    WRITE_EXTERNAL_STORAGE to WRITE_TO_STORAGE_PERMISSION_REQUEST_CODE
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

private fun permissionDenied(context: Context, permission: String) =
    ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_DENIED
package com.example.googlevision.util

import android.Manifest.permission.*
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

/**
 * Created by josephmagara on 19/2/19.
 */

private val permissionList = mapOf(CAMERA to 7, READ_EXTERNAL_STORAGE to 13, WRITE_EXTERNAL_STORAGE to 17)

fun Activity.hasAllNeededPermissions(permissions: Map<String, Int> = permissionList): Boolean =
    permissionList.keys.all { !permissionDenied(this, it) }

fun Activity.requestPermissionsIfNotRequested(
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
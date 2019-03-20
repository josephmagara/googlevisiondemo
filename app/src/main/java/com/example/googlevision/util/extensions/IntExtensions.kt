package com.example.googlevision.util.extensions

/**
 * Created by josephmagara on 19/2/19.
 */

fun Int.containsPermission() : Boolean =
    permissionList.values.any { this % it == 0 }

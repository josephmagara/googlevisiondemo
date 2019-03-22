package com.example.googlevision.util.extensions

/**
 * Created by josephmagara on 19/2/19.
 */

fun Int.containsPermission(): Boolean =
        permissionList.values.any { this % it == 0 }

fun Float.isGreaterThanSignedComparison(floatToCompare: Float) =
    if (isNegative()){
        this in floatToCompare..0f
    }else{
        this > floatToCompare
    }


private fun Number.isNegative(): Boolean = this.toDouble() < 0

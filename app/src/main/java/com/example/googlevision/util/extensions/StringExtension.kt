package com.example.googlevision.util.extensions

/**
 * Created by josephmagara on 20/2/19.
 */

fun String.withDefaultValueIfNeeded(): String {
    if (this.isBlank()){
        return "Empty string"
    }
    return this
}
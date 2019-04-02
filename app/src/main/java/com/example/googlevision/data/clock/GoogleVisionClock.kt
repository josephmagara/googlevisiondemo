package com.example.googlevision.data.clock

import javax.inject.Singleton

/**
 * Created by josephmagara on 2/4/19.
 */
@Singleton
class GoogleVisionClock {
    fun getTime() = System.currentTimeMillis()
}
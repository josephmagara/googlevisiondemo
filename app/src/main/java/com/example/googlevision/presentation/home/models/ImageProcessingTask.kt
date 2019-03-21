package com.example.googlevision.presentation.home.models

import android.media.Image

/**
 * Created by josephmagara on 20/3/19.
 */
@Suppress("ArrayInDataClass")
data class ImageProcessingTask(val image: Image, val rotation: Int)
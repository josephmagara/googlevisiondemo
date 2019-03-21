package com.example.googlevision.presentation

import android.media.Image

/**
 * Created by josephmagara on 21/2/19.
 */
interface ImageRetrievalPipeline {
    fun onImageReceived(image: Image, cameraId: String)
}
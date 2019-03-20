package com.example.googlevision.presentation

/**
 * Created by josephmagara on 21/2/19.
 */
interface ImageRetrievalPipeline {
    fun onImageReceived(byteArray: ByteArray, cameraId: String)
}
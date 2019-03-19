package com.example.googlevision.data.imageprocessor.interfaces

import android.graphics.Bitmap
import android.media.Image

/**
 * Created by josephmagara on 20/2/19.
 */
interface ImageProcessActioner {
    fun processImage(image:Image, rotation: Int)
    fun extractTextFromImage(bitmap: Bitmap, rotation: Int)
}
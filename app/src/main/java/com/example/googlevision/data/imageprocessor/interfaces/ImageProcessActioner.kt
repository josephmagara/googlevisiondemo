package com.example.googlevision.data.imageprocessor.interfaces

import android.graphics.Bitmap

/**
 * Created by josephmagara on 20/2/19.
 */
interface ImageProcessActioner {
    fun extractTextFromImage(bitmap: Bitmap, rotation: Int)
}
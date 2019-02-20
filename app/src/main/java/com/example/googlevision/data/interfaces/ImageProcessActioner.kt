package com.example.googlevision.data.interfaces

import android.graphics.Bitmap

/**
 * Created by josephmagara on 20/2/19.
 */
interface ImageProcessActioner {
    fun extractTextFromImage(bitmap: Bitmap, rotation: Int)
}
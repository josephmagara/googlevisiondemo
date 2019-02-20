package com.example.googlevision

import android.graphics.Bitmap
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import java.nio.ByteBuffer


/**
 * Created by josephmagara on 20/2/19.
 */
class ImageProcessor() {

    fun extractTextFromImage(bitmap: Bitmap, rotation: Int) {
        val firebaseVisionImage = getFireBaseVisionImage(bitmap, rotation)
    }

    private fun getFireBaseVisionImage(bitmap: Bitmap, rotation: Int): FirebaseVisionImage {

        val metadata = FirebaseVisionImageMetadata.Builder()
            .setWidth(bitmap.width)
            .setHeight(bitmap.height)
            .setRotation(rotation)
            .build()

        val bytes = bitmap.byteCount
        val byteBuffer = ByteBuffer.allocate(bytes)

        return FirebaseVisionImage.fromByteBuffer(byteBuffer, metadata)
    }

}
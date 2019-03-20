package com.example.googlevision.data

import android.graphics.Bitmap
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata

/**
 * Created by josephmagara on 21/2/19.
 */
abstract class FireBaseProcessor {

    fun getFireBaseVisionFromBitmap(bitmap: Bitmap): FirebaseVisionImage =
        FirebaseVisionImage.fromBitmap(bitmap)

    fun getFireBaseVisionFromByteArray(byteArray: ByteArray, rotation: Int): FirebaseVisionImage {
        val metadata = FirebaseVisionImageMetadata.Builder()
            .setWidth(480)
            .setHeight(360)
            .setRotation(rotation)
            .setFormat(FirebaseVisionImageMetadata.IMAGE_FORMAT_YV12)
            .build()

        return FirebaseVisionImage.fromByteArray(byteArray, metadata)
    }
}
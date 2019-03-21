package com.example.googlevision.data

import android.graphics.Bitmap
import android.media.Image
import com.google.firebase.ml.vision.common.FirebaseVisionImage

/**
 * Created by josephmagara on 21/2/19.
 */
abstract class FireBaseProcessor {

    fun getFireBaseVisionFromBitmap(bitmap: Bitmap): FirebaseVisionImage =
        FirebaseVisionImage.fromBitmap(bitmap)

    fun getFireBaseVisionFromImage(image: Image, rotation: Int): FirebaseVisionImage {
        return FirebaseVisionImage.fromMediaImage(image, rotation)
    }
}
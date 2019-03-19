package com.example.googlevision.data

import android.graphics.Bitmap
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import java.io.ByteArrayOutputStream

/**
 * Created by josephmagara on 21/2/19.
 */
abstract class FireBaseProcessor {

    fun getFireBaseVisionImage(bitmap: Bitmap, rotation: Int): FirebaseVisionImage {

        /*val metadata = FirebaseVisionImageMetadata.Builder()
            .setWidth(480)
            .setHeight(360)
            .setRotation(rotation)
            .setFormat(FirebaseVisionImageMetadata.YUV_420_888)
            .build()*/

        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)

        return FirebaseVisionImage.fromBitmap(bitmap)
    }
}
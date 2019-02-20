package com.example.googlevision.data.imageprocessor

import android.graphics.Bitmap
import com.example.googlevision.data.interfaces.ImageProcessActioner
import com.example.googlevision.data.interfaces.ImageProcessorObserver
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import com.google.firebase.ml.vision.text.FirebaseVisionText
import io.reactivex.Observable
import io.reactivex.processors.PublishProcessor
import java.nio.ByteBuffer
import javax.inject.Inject


/**
 * Created by josephmagara on 20/2/19.
 */
class ImageProcessor @Inject constructor(): ImageProcessActioner, ImageProcessorObserver {

    private val resultProcessor = PublishProcessor.create<FirebaseVisionText>()

    override fun extractTextFromImage(bitmap: Bitmap, rotation: Int) {
        val firebaseVisionImage = getFireBaseVisionImage(bitmap, rotation)

        val detector = FirebaseVision.getInstance()
            .onDeviceTextRecognizer

        val result = detector.processImage(firebaseVisionImage)
            .addOnSuccessListener { firebaseVisionText ->
                resultProcessor.onNext(firebaseVisionText)
            }
            .addOnFailureListener {
                resultProcessor.onError(it)
            }
    }

    override fun resultObserver(): Observable<FirebaseVisionText> = resultProcessor.toObservable()

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
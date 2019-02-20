package com.example.googlevision.data.imageprocessor

import android.graphics.Bitmap
import com.example.googlevision.data.interfaces.ImageProcessActioner
import com.example.googlevision.data.interfaces.ImageProcessorObserver
import com.example.googlevision.util.extensions.withDefaultValueIfNeeded
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata.IMAGE_FORMAT_YV12
import com.google.firebase.ml.vision.text.FirebaseVisionText
import io.reactivex.Observable
import io.reactivex.processors.PublishProcessor
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created by josephmagara on 20/2/19.
 */
@Singleton
class ImageProcessor @Inject constructor(): ImageProcessActioner, ImageProcessorObserver {

    private val resultProcessor = PublishProcessor.create<FirebaseVisionText>()

    override fun extractTextFromImage(bitmap: Bitmap, rotation: Int) {
        val firebaseVisionImage = getFireBaseVisionImage(bitmap, rotation)

        val detector = FirebaseVision.getInstance()
            .onDeviceTextRecognizer

        detector.processImage(firebaseVisionImage)
            .addOnSuccessListener { firebaseVisionText ->
                Timber.d(firebaseVisionImage.toString().withDefaultValueIfNeeded())
                resultProcessor.onNext(firebaseVisionText)
            }
            .addOnFailureListener {
                resultProcessor.onError(it)
            }
    }

    override fun resultObserver(): Observable<FirebaseVisionText> = resultProcessor.toObservable()

    private fun getFireBaseVisionImage(bitmap: Bitmap, rotation: Int): FirebaseVisionImage {

        val metadata = FirebaseVisionImageMetadata.Builder()
            .setWidth(480)
            .setHeight(360)
            .setRotation(rotation)
            .setFormat(IMAGE_FORMAT_YV12)
            .build()

        val bytes = bitmap.byteCount
        val byteBuffer = ByteBuffer.allocate(bytes)
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        byteBuffer.put(stream.toByteArray())

        return FirebaseVisionImage.fromByteBuffer(byteBuffer, metadata)
    }

}
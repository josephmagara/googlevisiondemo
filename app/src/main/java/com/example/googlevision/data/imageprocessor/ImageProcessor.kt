package com.example.googlevision.data.imageprocessor

import android.graphics.Bitmap
import com.example.googlevision.data.FireBaseProcessor
import com.example.googlevision.data.imageprocessor.interfaces.ImageProcessActioner
import com.example.googlevision.data.imageprocessor.interfaces.ImageProcessorObserver
import com.example.googlevision.util.extensions.withDefaultValueIfNeeded
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.text.FirebaseVisionText
import io.reactivex.Observable
import io.reactivex.processors.PublishProcessor
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Created by josephmagara on 20/2/19.
 */
@Singleton
class ImageProcessor @Inject constructor(): FireBaseProcessor(), ImageProcessActioner, ImageProcessorObserver {

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

    override fun imageProcessResultObserver(): Observable<FirebaseVisionText> = resultProcessor.toObservable()



}
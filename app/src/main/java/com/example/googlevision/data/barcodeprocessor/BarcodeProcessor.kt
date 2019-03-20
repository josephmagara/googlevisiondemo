package com.example.googlevision.data.barcodeprocessor

import android.graphics.Bitmap
import com.example.googlevision.data.FireBaseProcessor
import com.example.googlevision.data.barcodeprocessor.interfaces.BarcodeProcessActioner
import com.example.googlevision.data.barcodeprocessor.interfaces.BarcodeProcessObserver
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode
import io.reactivex.Observable
import io.reactivex.processors.PublishProcessor
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by josephmagara on 21/2/19.
 */
@Singleton
class BarcodeProcessor @Inject constructor() : FireBaseProcessor(), BarcodeProcessObserver, BarcodeProcessActioner {

    private val resultProcessor = PublishProcessor.create<List<FirebaseVisionBarcode>>()

    override fun extractInformationFromBarcode(barcodeBitmap: Bitmap, rotation: Int) {

        val firebaseVisionImage = getFireBaseVisionFromBitmap(barcodeBitmap)

        val detector = FirebaseVision.getInstance()
            .visionBarcodeDetector

        detector.detectInImage(firebaseVisionImage)
            .addOnSuccessListener { barcodes ->
                resultProcessor.onNext(barcodes)
            }
            .addOnFailureListener {
                resultProcessor.onError(it)
            }
    }

    override fun barcodeProcessResultObserver(): Observable<List<FirebaseVisionBarcode>> =
        resultProcessor.toObservable()


}
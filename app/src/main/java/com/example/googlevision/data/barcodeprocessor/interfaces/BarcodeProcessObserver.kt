package com.example.googlevision.data.barcodeprocessor.interfaces

import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode
import io.reactivex.Observable

/**
 * Created by josephmagara on 21/2/19.
 */
interface BarcodeProcessObserver {
    fun barcodeProcessResultObserver(): Observable<List<FirebaseVisionBarcode>>
}
package com.example.googlevision.data.imageprocessor.interfaces

import com.google.firebase.ml.vision.text.FirebaseVisionText
import io.reactivex.Observable

/**
 * Created by josephmagara on 20/2/19.
 */
interface ImageProcessorObserver {
    fun imageProcessResultObserver(): Observable<FirebaseVisionText>
}
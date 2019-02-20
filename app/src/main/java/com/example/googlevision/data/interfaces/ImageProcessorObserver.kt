package com.example.googlevision.data.interfaces

import com.google.firebase.ml.vision.text.FirebaseVisionText
import io.reactivex.Observable

/**
 * Created by josephmagara on 20/2/19.
 */
interface ImageProcessorObserver {
    fun resultObserver(): Observable<FirebaseVisionText>
}
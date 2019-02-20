package com.example.googlevision.presentation.home

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.googlevision.data.imageprocessor.interfaces.ImageProcessActioner
import com.example.googlevision.data.imageprocessor.interfaces.ImageProcessorObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposables
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * Created by josephmagara on 19/2/19.
 */
class HomeViewModel @Inject constructor(
    private val imageProcessActioner: ImageProcessActioner,
    imageProcessorObserver: ImageProcessorObserver
) : ViewModel() {

    private var addImageAction = MutableLiveData<Any>()
    private var processedText = MutableLiveData<String>()
    private var imageProcessedDisposable = Disposables.disposed()

    init {
        imageProcessedDisposable = imageProcessorObserver.imageProcessResultObserver()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.computation())
            .subscribe {
                processedText.value = it.text
            }
    }

    override fun onCleared() {
        imageProcessedDisposable.dispose()
        super.onCleared()
    }

    fun triggerAddImageAction() {
        addImageAction.value = true
    }

    fun addImageAction(): LiveData<Any> = addImageAction

    fun processedText(): LiveData<String> = processedText

    fun extractImageFromText(bitmap: Bitmap, imageRotation: Int) =
        imageProcessActioner.extractTextFromImage(bitmap, imageRotation)
}
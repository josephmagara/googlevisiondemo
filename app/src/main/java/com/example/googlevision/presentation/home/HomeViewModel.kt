package com.example.googlevision.presentation.home

import android.graphics.Bitmap
import android.media.Image
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.googlevision.data.barcodeprocessor.interfaces.BarcodeProcessActioner
import com.example.googlevision.data.imageprocessor.interfaces.ImageProcessActioner
import com.example.googlevision.data.imageprocessor.interfaces.ImageProcessorObserver
import com.example.googlevision.domain.models.GvBarcode
import com.example.googlevision.domain.usecases.ProcessBarcodeUseCase
import com.example.googlevision.presentation.home.models.ImageProcessingTask
import com.example.googlevision.util.extensions.cast
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposables
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Created by josephmagara on 19/2/19.
 */
class HomeViewModel @Inject constructor(
    private val imageProcessActioner: ImageProcessActioner,
    private val barcodeProcessActioner: BarcodeProcessActioner,
    imageProcessorObserver: ImageProcessorObserver,
    processBarcodeUseCase: ProcessBarcodeUseCase
) : ViewModel() {

    companion object {
        private const val IMAGE_CAPTURE_DELAY = 1000L
    }

    private var captureImage = MutableLiveData<Any>()
    private var addImageAction = MutableLiveData<Any>()
    private var processedText = MutableLiveData<String>()
    private val imageProcessingTaskList = mutableListOf<ImageProcessingTask>()

    private var compositeDisposable = CompositeDisposable()
    private var imageProcessedDisposable = Disposables.disposed()
    private var barcodeProcessedDisposable = Disposables.disposed()
    private var imageProcessingTaskListObserver = Disposables.disposed()

    init {

        imageProcessingTaskListObserver = Observable.fromIterable(imageProcessingTaskList)
            .delay(200, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.computation())
            .subscribe {
                Timber.v("Starting to import image")
                imageProcessActioner.processImage(it.image, it.rotation)
            }

        imageProcessedDisposable = imageProcessorObserver.imageProcessResultObserver()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.computation())
            .subscribe { label ->
                val stringBuilder = StringBuilder()
                label.forEach {
                    stringBuilder.append(it.text + " ")
                }
                processedText.value = stringBuilder.toString()
            }

        barcodeProcessedDisposable = processBarcodeUseCase.results()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.computation())
            .subscribe {
                processedText.value = formatBarcodesForDisplay(it)
            }

        compositeDisposable.addAll(imageProcessingTaskListObserver, imageProcessedDisposable, barcodeProcessedDisposable)
    }

    private fun formatBarcodesForDisplay(barcodes: List<GvBarcode>): String {
        var displayString = "Results: \n"
        barcodes.forEachIndexed { index, barcode ->
            var barcodeString = "$index. Type: ${barcode.type}\n"
            barcode.information.forEach { barcodeInformation ->
                barcodeInformation.value?.let {
                    val castInfo = cast(it, barcodeInformation.primitiveType)
                    barcodeString += "${barcodeInformation.description} : $castInfo\n"
                }
            }
            displayString += barcodeString
        }
        return displayString
    }

    override fun onCleared() {
        imageProcessedDisposable.dispose()
        super.onCleared()
    }

    fun triggerAddImageAction() {
        addImageAction.value = true
    }

    @Deprecated("This will be pulled out soon, use capture image instead")
    fun addImageAction(): LiveData<Any> = addImageAction

    fun captureImage(): LiveData<Any> = captureImage

    fun processedText(): LiveData<String> = processedText

    fun queueImageForProcessing(image: Image, rotation: Int) {
        imageProcessingTaskList.add(ImageProcessingTask(image, rotation))
    }

    fun extractTextFromImage(bitmap: Bitmap, imageRotation: Int) =
        imageProcessActioner.extractTextFromImage(bitmap, imageRotation)

    fun extractInformationFromBarcode(bitmap: Bitmap, rotation: Int) =
        barcodeProcessActioner.extractInformationFromBarcode(bitmap, rotation)
}
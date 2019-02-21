package com.example.googlevision.presentation.home

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.googlevision.data.barcodeprocessor.interfaces.BarcodeProcessActioner
import com.example.googlevision.data.imageprocessor.interfaces.ImageProcessActioner
import com.example.googlevision.data.imageprocessor.interfaces.ImageProcessorObserver
import com.example.googlevision.domain.models.GvBarcode
import com.example.googlevision.domain.usecases.ProcessBarcodeUseCase
import com.example.googlevision.util.extensions.cast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposables
import io.reactivex.schedulers.Schedulers
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

    private var addImageAction = MutableLiveData<Any>()
    private var processedText = MutableLiveData<String>()
    private var imageProcessedDisposable = Disposables.disposed()
    private var barcodeProcessedDisposable = Disposables.disposed()

    init {
        imageProcessedDisposable = imageProcessorObserver.imageProcessResultObserver()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.computation())
            .subscribe {
                processedText.value = it.text
            }
        barcodeProcessedDisposable = processBarcodeUseCase.results()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.computation())
            .subscribe {
                processedText.value = formatBarcodesForDisplay(it)
            }
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

    fun addImageAction(): LiveData<Any> = addImageAction

    fun processedText(): LiveData<String> = processedText

    fun extractTextFromImage(bitmap: Bitmap, imageRotation: Int) =
        imageProcessActioner.extractTextFromImage(bitmap, imageRotation)

    fun extractInformationFromBarcode(bitmap: Bitmap, rotation: Int) =
        barcodeProcessActioner.extractInformationFromBarcode(bitmap, rotation)
}
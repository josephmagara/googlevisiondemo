package com.example.googlevision.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.googlevision.data.imageprocessor.interfaces.ImageProcessActioner
import com.example.googlevision.data.imageprocessor.interfaces.ImageProcessorObserver
import com.example.googlevision.domain.usecases.ProcessBarcodeUseCase
import javax.inject.Inject

/**
 * Created by josephmagara on 19/2/19.
 */
@Suppress("UNCHECKED_CAST")
class HomeViewModelFactory @Inject constructor(
    private val imageProcessActioner: ImageProcessActioner,
    private val imageProcessorObserver: ImageProcessorObserver,
    private val processBarcodeUseCase: ProcessBarcodeUseCase
) :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return HomeViewModel(imageProcessActioner, imageProcessorObserver, processBarcodeUseCase) as T
    }
}

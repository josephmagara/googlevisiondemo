package com.example.googlevision.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.googlevision.data.imageprocessor.interfaces.ImageProcessActioner
import com.example.googlevision.data.imageprocessor.interfaces.ImageProcessorObserver
import javax.inject.Inject

/**
 * Created by josephmagara on 19/2/19.
 */
@Suppress("UNCHECKED_CAST")
class HomeViewModelFactory @Inject constructor(
    private val imageProcessActioner: ImageProcessActioner,
    private val imageProcessorObserver: ImageProcessorObserver
) :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return HomeViewModel(imageProcessActioner, imageProcessorObserver) as T
    }
}

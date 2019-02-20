package com.example.googlevision.domain

import javax.inject.Inject

/**
 * Created by josephmagara on 20/2/19.
 */
class ImageProcessedUseCase @Inject constructor(private val imageProcessor: ImageProcessorObserver){

    fun result() = imageProcessor.resultObserver()
}
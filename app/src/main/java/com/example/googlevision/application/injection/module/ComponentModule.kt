package com.example.googlevision.application.injection.module

import com.example.googlevision.data.barcodeprocessor.BarcodeProcessor
import com.example.googlevision.data.barcodeprocessor.interfaces.BarcodeProcessActioner
import com.example.googlevision.data.barcodeprocessor.interfaces.BarcodeProcessObserver
import com.example.googlevision.data.imageprocessor.ImageProcessor
import com.example.googlevision.data.imageprocessor.interfaces.ImageProcessActioner
import com.example.googlevision.data.imageprocessor.interfaces.ImageProcessorObserver
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by josephmagara on 20/2/19.
 */

@Module
class ComponentModule {

    @Singleton
    @Provides
    fun provideImageProcessorObservable(imageProcessor: ImageProcessor): ImageProcessorObserver = imageProcessor

    @Singleton
    @Provides
    fun provideImageProcessActioner(imageProcessor: ImageProcessor): ImageProcessActioner = imageProcessor

    @Singleton
    @Provides
    fun provideBarcodeProcessorObservable(barcodeProcessor: BarcodeProcessor): BarcodeProcessObserver = barcodeProcessor

    @Singleton
    @Provides
    fun provideBarcodeProcessActioner(barcodeProcessor: BarcodeProcessor): BarcodeProcessActioner = barcodeProcessor

}
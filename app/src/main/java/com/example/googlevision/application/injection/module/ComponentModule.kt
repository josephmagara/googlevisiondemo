package com.example.googlevision.application.injection.module

import com.example.googlevision.data.imageprocessor.ImageProcessor
import com.example.googlevision.data.interfaces.ImageProcessActioner
import com.example.googlevision.data.interfaces.ImageProcessorObserver
import dagger.Module
import dagger.Provides

/**
 * Created by josephmagara on 20/2/19.
 */

@Module
class ComponentModule {

    @Provides
    fun provideImageProcessorObservable(imageProcessor: ImageProcessor): ImageProcessorObserver = imageProcessor

    @Provides
    fun provideImageProcessActioner(imageProcessor: ImageProcessor): ImageProcessActioner = imageProcessor

}
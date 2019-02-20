package com.example.googlevision.application.injection.module

import com.example.googlevision.data.imageprocessor.ImageProcessor
import com.example.googlevision.data.interfaces.ImageProcessActioner
import com.example.googlevision.data.interfaces.ImageProcessorObserver
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

}
package com.example.googlevision.application

import com.example.googlevision.application.injection.component.DaggerApplicationComponent
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication

/**
 * Created by josephmagara on 19/2/19.
 */
class GoogleVisionApplication : DaggerApplication() {
    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerApplicationComponent.builder()
            .bindApplication(this)
            .build()
    }
}
package com.example.googlevision.application

import com.example.googlevision.BuildConfig
import com.example.googlevision.application.injection.component.DaggerApplicationComponent
import com.google.firebase.FirebaseApp
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import timber.log.Timber



/**
 * Created by josephmagara on 19/2/19.
 */
class GoogleVisionApplication : DaggerApplication() {
    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerApplicationComponent.builder()
            .bindApplication(this)
            .build()
    }

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
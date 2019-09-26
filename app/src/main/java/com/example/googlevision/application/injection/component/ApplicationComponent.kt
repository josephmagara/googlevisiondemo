package com.example.googlevision.application.injection.component

import android.app.Application
import com.example.googlevision.application.GoogleVisionApplication
import com.example.googlevision.application.injection.module.*
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

/**
 * Created by josephmagara on 19/2/19.
 */
@Singleton
@Component(modules = [AndroidSupportInjectionModule::class, ApplicationModule::class, ActivityModule::class,
    ComponentModule::class, MotionModule::class, UsecaseModule::class])
interface ApplicationComponent : AndroidInjector<GoogleVisionApplication> {

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun bindApplication(application: Application): Builder

        fun build(): ApplicationComponent
    }
}
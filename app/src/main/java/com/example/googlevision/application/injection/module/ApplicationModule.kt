package com.example.googlevision.application.injection.module

import android.app.Activity
import android.app.Application
import android.content.Context
import com.example.googlevision.presentation.home.HomeActivity
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

/**
 * Created by josephmagara on 19/2/19.
 */
@Module
abstract class ApplicationModule {
    @Binds
    @Singleton
    abstract fun bindApplicationContext(application: Application): Context

    @Binds
    @Singleton
    abstract fun bindHomeActivity(homeActivity: HomeActivity): Activity
}

package com.example.googlevision.application.injection.module

import com.example.googlevision.presentation.home.HomeActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Created by josephmagara on 20/2/19.
 */

@Module
internal abstract class ActivityModule {

    @ContributesAndroidInjector(modules = [HomeModule::class])
    internal abstract fun injectHomeActivity(): HomeActivity
}
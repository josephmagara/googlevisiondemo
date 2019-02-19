package com.example.googlevision.application.injection.module

import android.app.Activity
import com.example.googlevision.presentation.home.HomeActivity
import dagger.Module
import dagger.Provides

/**
 * Created by josephmagara on 20/2/19.
 */
@Module
class HomeModule{
    @Provides
    fun providesActivity(activity: HomeActivity): Activity = activity
}
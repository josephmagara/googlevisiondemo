package com.example.googlevision.application.injection.component

import android.app.Activity
import dagger.Subcomponent
import dagger.android.AndroidInjector

/**
 * Created by josephmagara on 20/2/19.
 */
@Subcomponent()
interface ActivityComponent: AndroidInjector<Activity> {

    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<Activity>()
}
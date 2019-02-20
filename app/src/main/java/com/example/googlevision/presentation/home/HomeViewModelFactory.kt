package com.example.googlevision.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject

/**
 * Created by josephmagara on 19/2/19.
 */
@Suppress("UNCHECKED_CAST")
class HomeViewModelFactory @Inject constructor() :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return HomeViewModel() as T
    }
}

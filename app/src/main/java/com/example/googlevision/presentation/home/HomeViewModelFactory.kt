package com.example.googlevision.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.googlevision.domain.CreateFileUseCase
import javax.inject.Inject

/**
 * Created by josephmagara on 19/2/19.
 */
@Suppress("UNCHECKED_CAST")
class HomeViewModelFactory @Inject constructor(private val createFileUseCase: CreateFileUseCase) :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return HomeViewModelFactory(createFileUseCase) as T
    }
}

package com.example.googlevision.presentation.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import javax.inject.Inject

/**
 * Created by josephmagara on 19/2/19.
 */
class HomeViewModel @Inject constructor() : ViewModel() {


    private var addImageAction = MutableLiveData<Any>()

    fun triggerAddImageAction() {
        addImageAction.value = true
    }

    fun addImageAction(): LiveData<Any> = addImageAction
}
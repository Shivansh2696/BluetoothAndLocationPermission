package com.example.bluetooth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel:ViewModel() {
    val permissionRequested:MutableLiveData<Boolean> = MutableLiveData()
    init {
        permissionRequested.value=false
    }
}
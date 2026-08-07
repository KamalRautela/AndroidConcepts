package com.example.androidconcepts.mvvm.view_model_scope

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ViewModelScopeViewModel : ViewModel() {
    private val _seconds = MutableLiveData(0)
    val seconds : LiveData<Int> = _seconds
    private var job : Job? = null

    fun start() {
        job = viewModelScope.launch {
            while (true) {
                delay(1000)
                _seconds.value = (_seconds.value ?: 0) + 1
            }
        }
    }
    fun stop() {
        job?.cancel()
    }

    fun reset() {
        job?.cancel()
        _seconds.value = 0
    }
}
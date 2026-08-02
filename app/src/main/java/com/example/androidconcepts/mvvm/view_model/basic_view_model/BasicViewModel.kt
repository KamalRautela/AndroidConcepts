package com.example.androidconcepts.mvvm.view_model.basic_view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BasicViewModel : ViewModel() {
    private val _counter = MutableLiveData(0)
    val counter : LiveData<Int> = _counter

    fun incrementCounter() {
        _counter.value = (_counter.value ?: 0) + 1
    }

    fun decrementCounter() {
        _counter.value = (_counter.value ?: 0) - 1
    }

    fun resetCounter() {
        _counter.value = 0
    }
}
package com.example.androidconcepts.mvvm.view_model.livedata

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map

class LiveDataViewModel : ViewModel() {
    private val _celsius = MutableLiveData("")
    val celsius : LiveData<String> = _celsius

    val fahrenheit : LiveData<String> = _celsius.map {
        val c = it.toDoubleOrNull()
        if (c != null) "${(c * 9.0/5.0) + 32}" else ""
    }

    fun setCelsius(value : String) {
        val num = value.toDoubleOrNull()
        _celsius.value = when {
            num == null -> value
            num <= 50000 -> value
            else -> "Exceeds 50000"
        }
    }
}
package com.example.androidconcepts.mvvm.view_model.stateFlow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class StateFlowViewModel : ViewModel() {
    private val _userName = MutableStateFlow("")
    val userName : StateFlow<String> = _userName.asStateFlow()

    val isValid : StateFlow<Boolean> = _userName
        .map { it.length in 3..20 && it.matches(Regex("[a-zA-Z0-9_]+")) }
        .stateIn(viewModelScope, SharingStarted.Eagerly,false)

    fun setUserName(value : String) {
        _userName.value = value.trim()
    }
}
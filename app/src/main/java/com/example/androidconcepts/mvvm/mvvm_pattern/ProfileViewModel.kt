package com.example.androidconcepts.mvvm.mvvm_pattern

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {
    private val _userLiveData = MutableLiveData<User>()
    val userLiveData : LiveData<User> = _userLiveData

    fun loadUser() {
        viewModelScope.launch {
            delay(1000)
            _userLiveData.value = User(1,"Kamal",25,"kamalrautela@gmail.com")
        }
    }

    fun incrementAge() {
        _userLiveData.value = _userLiveData.value?.copy(age = _userLiveData.value!!.age + 1)
    }
}
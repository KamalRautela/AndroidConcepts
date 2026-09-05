package com.example.androidconcepts.sharedPreference

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class SharedPrefsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: SharedPrefsRepository

    private val _username = MutableLiveData<String>()
    val username: LiveData<String> = _username

    private val _isLoggedIn = MutableLiveData<Boolean>()
    val isLoggedIn: LiveData<Boolean> = _isLoggedIn

    init {
        val prefs = application.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        repository = SharedPrefsRepository(prefs)
        loadData()
    }

    private fun loadData() {
        _username.value = repository.getUsername()
        _isLoggedIn.value = repository.isLoggedIn()
    }

    fun save(username: String, isLoggedIn: Boolean) {
        repository.saveUsername(username, isLoggedIn)
        loadData()
    }

    fun clear() {
        repository.clear()
        loadData()
    }
}

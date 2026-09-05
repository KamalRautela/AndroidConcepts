package com.example.androidconcepts.datastore

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class DatastoreViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = DatastoreRepository(application.datastore)

    val userName : LiveData<String> = repository.userName.asLiveData()
    val isLoggedIn : LiveData<Boolean> = repository.isLoggedIn.asLiveData()

    fun saveData(userName : String,isLoggedIn : Boolean) {
        viewModelScope.launch {
            repository.saveData(userName, isLoggedIn)
        }
    }

        fun clear() {
            viewModelScope.launch { repository.clear() }
        }
    }
package com.example.androidconcepts.hilt.retrofit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidconcepts.json_parsing.retrofit.get_api.GetApiResource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommentViewModel @Inject constructor(private val repository: CommentRepository) : ViewModel() {
    private val _commentLiveData = MutableLiveData<GetApiResource<List<Comment>>>()
    val commentLiveData : LiveData<GetApiResource<List<Comment>>> = _commentLiveData

    fun getComments() {
        _commentLiveData.postValue(GetApiResource.Loading)
        viewModelScope.launch {
            try {
                val response = repository.getComments()
                _commentLiveData.postValue(GetApiResource.Success(response))
            } catch (e: Exception) {
                _commentLiveData.postValue(GetApiResource.Error(e.message ?: "An unknown error occurred"))
            }
        }
    }
}
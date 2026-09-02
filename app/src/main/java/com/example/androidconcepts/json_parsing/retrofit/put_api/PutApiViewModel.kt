package com.example.androidconcepts.json_parsing.retrofit.put_api

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidconcepts.json_parsing.retrofit.get_api.GetApiResource
import com.example.androidconcepts.json_parsing.retrofit.get_api.Post
import kotlinx.coroutines.launch

class PutApiViewModel(
    private val repository: PutApiRepository = PutApiRepository()
) : ViewModel() {
    private val _updatePostLiveData = MutableLiveData<GetApiResource<Post>>()
    val updatePostLiveData : LiveData<GetApiResource<Post>> = _updatePostLiveData

    fun updatePost(id : Int,title : String,body : String) {
        _updatePostLiveData.value = GetApiResource.Loading
        viewModelScope.launch {
            try {
                val updatedPost = Post(userId = 1, id = id, title = title, body = body)
                _updatePostLiveData.value = GetApiResource.Success(repository.updatePost(id,updatedPost))
            } catch (e : Exception) {
                _updatePostLiveData.value = GetApiResource.Error(e.message ?: "Unknown error")
            }
        }
    }
}
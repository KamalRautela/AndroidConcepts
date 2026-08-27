package com.example.androidconcepts.json_parsing.retrofit.get_api

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class GetApiViewModel(
    private val repository : GetApiRepository = GetApiRepository()
) : ViewModel() {
    private val _post = MutableLiveData<GetApiResource<Post>>()
    val post : LiveData<GetApiResource<Post>> = _post

    private val _postList = MutableLiveData<GetApiResource<List<Post>>>()
    val postList : LiveData<GetApiResource<List<Post>>> = _postList

    fun loadPostById(id : Int) {
        _post.value = GetApiResource.Loading
        viewModelScope.launch {
            try {
                _post.value = GetApiResource.Success(repository.getPostById(id))
            } catch (e : Exception) {
                _post.value = GetApiResource.Error(e.message ?: "Unknown Error")
            }
        }
    }

    fun loadPostListByUserId(userId : Int) {
        _postList.value = GetApiResource.Loading
        viewModelScope.launch {
            try {
                _postList.value = GetApiResource.Success(repository.getPostsByUserId(userId))
            } catch (e : Exception) {
                _postList.value = GetApiResource.Error(e.message ?: "Unknown Error")
            }
        }
    }
}
package com.example.androidconcepts.json_parsing.retrofit.post_api

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidconcepts.json_parsing.retrofit.get_api.GetApiResource
import com.example.androidconcepts.json_parsing.retrofit.get_api.Post
import kotlinx.coroutines.launch

class PostApiViewModel(
    private val repository: PostApiRepository = PostApiRepository()
) : ViewModel() {
    private val _createPostData = MutableLiveData<GetApiResource<Post>>()
    val createPostData: LiveData<GetApiResource<Post>> = _createPostData

    fun createPost(title: String, body: String) {
        _createPostData.value = GetApiResource.Loading
        viewModelScope.launch {
            try {
                val newPost = NewPost(userId = 1, title = title, body = body)
                _createPostData.value = GetApiResource.Success(repository.createPost(newPost))
            } catch (e : Exception) {
                _createPostData.value = GetApiResource.Error(e.message ?: "Unknown Error")
            }
        }
    }
}
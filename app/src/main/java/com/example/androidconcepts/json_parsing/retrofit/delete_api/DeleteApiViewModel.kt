package com.example.androidconcepts.json_parsing.retrofit.delete_api

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidconcepts.json_parsing.retrofit.get_api.GetApiResource
import kotlinx.coroutines.launch

class DeleteApiViewModel(
    private val repository: DeleteApiRepository = DeleteApiRepository()
) : ViewModel() {
    private val _deletePostData = MutableLiveData<GetApiResource<Unit>>()
    val deletePostData: LiveData<GetApiResource<Unit>> = _deletePostData

    fun deletePost(id: Int) {
        _deletePostData.value = GetApiResource.Loading
        viewModelScope.launch {
            try {
                repository.deletePost(id)
                _deletePostData.value = GetApiResource.Success(Unit)
            } catch (e: Exception) {
                _deletePostData.value = GetApiResource.Error(e.message ?: "Unknown Error")
            }
        }
    }
}

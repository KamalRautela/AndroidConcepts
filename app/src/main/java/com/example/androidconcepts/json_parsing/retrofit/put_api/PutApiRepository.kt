package com.example.androidconcepts.json_parsing.retrofit.put_api

import com.example.androidconcepts.common.ApiService
import com.example.androidconcepts.common.RetrofitInstance
import com.example.androidconcepts.json_parsing.retrofit.get_api.Post

class PutApiRepository(
    private val apiService: ApiService = RetrofitInstance.apiService
) {
    suspend fun updatePost(id : Int,post: Post) : Post {
        return apiService.updatePost(id = id, post = post)
    }
}
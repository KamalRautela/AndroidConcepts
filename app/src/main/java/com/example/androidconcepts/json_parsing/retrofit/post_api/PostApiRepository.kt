package com.example.androidconcepts.json_parsing.retrofit.post_api

import com.example.androidconcepts.common.ApiService
import com.example.androidconcepts.common.RetrofitInstance
import com.example.androidconcepts.json_parsing.retrofit.get_api.Post

class PostApiRepository(
    private val apiService: ApiService = RetrofitInstance.apiService
) {
    suspend fun createPost(newPost: NewPost) : Post {
        return apiService.createPost(newPost)
    }
}

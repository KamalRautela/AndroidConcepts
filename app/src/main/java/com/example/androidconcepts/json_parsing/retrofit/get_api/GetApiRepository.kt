package com.example.androidconcepts.json_parsing.retrofit.get_api

import com.example.androidconcepts.common.ApiService
import com.example.androidconcepts.common.RetrofitInstance

class GetApiRepository(
    private val apiService : ApiService = RetrofitInstance.apiService
) {
    suspend fun getPostById(id : Int) : Post {
        return apiService.getPostById(id)
    }

    suspend fun getPostsByUserId(userId : Int) : List<Post> {
        return apiService.getPostsByUserId(userId)
    }
}
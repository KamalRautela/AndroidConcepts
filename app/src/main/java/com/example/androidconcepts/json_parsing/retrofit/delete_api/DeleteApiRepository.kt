package com.example.androidconcepts.json_parsing.retrofit.delete_api

import com.example.androidconcepts.common.ApiService
import com.example.androidconcepts.common.RetrofitInstance

class DeleteApiRepository(
    private val apiService: ApiService = RetrofitInstance.apiService
) {
    suspend fun deletePost(id: Int) {
        apiService.deletePost(id)
    }
}

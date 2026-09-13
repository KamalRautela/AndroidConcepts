package com.example.androidconcepts.hilt.retrofit

import com.example.androidconcepts.common.ApiService
import javax.inject.Inject

class CommentRepository @Inject constructor(private val apiService: ApiService) {
    suspend fun getComments() : List<Comment> {
        return apiService.getComments()
    }
}
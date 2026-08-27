package com.example.androidconcepts.common

import com.example.androidconcepts.json_parsing.retrofit.get_api.Post
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("posts/{id}")
    suspend fun getPostById(@Path("id") id : Int) : Post

    @GET("posts")
    suspend fun getPostsByUserId(@Query("userId") userId : Int) : List<Post>
}
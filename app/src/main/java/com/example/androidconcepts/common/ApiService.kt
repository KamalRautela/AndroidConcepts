package com.example.androidconcepts.common

import com.example.androidconcepts.hilt.retrofit.Comment
import com.example.androidconcepts.json_parsing.retrofit.get_api.Post
import com.example.androidconcepts.json_parsing.retrofit.post_api.NewPost
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("posts/{id}")
    suspend fun getPostById(@Path("id") id : Int) : Post
    @GET("posts")
    suspend fun getPostsByUserId(@Query("userId") userId : Int) : List<Post>
    @POST("posts")
    suspend fun createPost(@Body newPost: NewPost) : Post
    @PUT("posts/{id}")
    suspend fun updatePost(@Path("id") id : Int,@Body post: Post) : Post
    @DELETE("posts/{id}")
    suspend fun deletePost(@Path("id") id : Int)

    @GET("comments")
    suspend fun getComments() : List<Comment>
}
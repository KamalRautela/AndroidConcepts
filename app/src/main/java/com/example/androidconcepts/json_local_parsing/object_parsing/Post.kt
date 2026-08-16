package com.example.androidconcepts.json_local_parsing.object_parsing

data class Post(
    val id : Int,
    val title : String,
    val body : String,
    val rating : Double,
    val isPublished : Boolean,
    val publishedAt : String?
)

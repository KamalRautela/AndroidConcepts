package com.example.androidconcepts.json_parsing.retrofit.get_api

sealed class GetApiResource<out T> {
    object Loading : GetApiResource<Nothing>()
    data class Success<T>(val data : T) : GetApiResource<T>()
    data class Error(val message : String) : GetApiResource<Nothing>()
}
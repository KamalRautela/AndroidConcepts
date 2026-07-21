package com.example.androidconcepts.activity_fragment_lifecycle.pass_data

import java.io.Serializable

data class User(
    val name : String,
    val age : Int,
    val isPremium : Boolean
) : Serializable

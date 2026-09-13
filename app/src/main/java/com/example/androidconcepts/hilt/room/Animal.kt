package com.example.androidconcepts.hilt.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "animal")
data class Animal(
    @PrimaryKey(autoGenerate = true) val id : Int = 0,
    val name : String,
    val species : String
)

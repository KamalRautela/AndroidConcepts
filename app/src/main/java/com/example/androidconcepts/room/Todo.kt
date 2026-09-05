package com.example.androidconcepts.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "todo")
data class Todo(
    @PrimaryKey(autoGenerate = true)
    val id : Int = 0,
    val title : String,
    val isDone : Boolean = false
)
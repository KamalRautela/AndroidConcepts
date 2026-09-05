package com.example.androidconcepts.room

import android.content.Context
import androidx.room.Room

object RoomInstance {
    @Volatile
    private var INSTANCE : TodoDatabase? = null

    fun getDatabase(context: Context) : TodoDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(context.applicationContext, TodoDatabase::class.java,"todo_database").build()
            INSTANCE = instance
            instance
        }
    }
}
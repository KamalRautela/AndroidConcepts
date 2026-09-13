package com.example.androidconcepts.hilt.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Animal::class], version = 1)
abstract class AnimalDatabase() : RoomDatabase() {
    abstract fun animalDao() : AnimalDao
}
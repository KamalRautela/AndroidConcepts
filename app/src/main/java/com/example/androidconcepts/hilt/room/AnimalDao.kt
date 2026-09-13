package com.example.androidconcepts.hilt.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AnimalDao {
    @Insert
    suspend fun insertAnimal(animal: Animal)

    @Delete
    suspend fun deleteAnimal(animal: Animal)

    @Update
    suspend fun updateAnimal(animal: Animal)

    @Query("Select * from animal")
    fun getAnimals() : Flow<List<Animal>>
}
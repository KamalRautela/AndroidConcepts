package com.example.androidconcepts.hilt.room

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AnimalRepository @Inject constructor(
    private val animalDao: AnimalDao
) {
    suspend fun insertAnimal(animal: Animal) {
        animalDao.insertAnimal(animal)
    }

    suspend fun deleteAnimal(animal: Animal) {
        animalDao.deleteAnimal(animal)
    }

    fun getAllAnimals(): Flow<List<Animal>> = animalDao.getAnimals()
}

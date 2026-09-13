package com.example.androidconcepts.hilt.room

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnimalViewModel @Inject constructor(
    private val repository: AnimalRepository
) : ViewModel() {

    val animalLiveData: LiveData<List<Animal>> = repository.getAllAnimals().asLiveData()

    fun insertAnimal(name: String, species: String) {
        viewModelScope.launch {
            repository.insertAnimal(Animal(name = name, species = species))
        }
    }

    fun deleteAnimal(animal: Animal) {
        viewModelScope.launch {
            repository.deleteAnimal(animal)
        }
    }
}

package com.example.androidconcepts.room

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class TodoViewModel(application: Application) : AndroidViewModel(application) {
    private val repository : TodoRepository
    val todoLiveData : LiveData<List<Todo>>

    init {
        val dao = RoomInstance.getDatabase(application).todoDao()
        repository = TodoRepository(dao)
        todoLiveData = repository.getAllTodos().asLiveData()
    }

    fun insertTodo(title : String) {
        viewModelScope.launch {
            repository.insertTodo(Todo(title = title))
        }
    }

    fun toggleDone(todo: Todo) {
        viewModelScope.launch {
            repository.updateTodo(todo.copy(isDone = !todo.isDone))
        }
    }

    fun deleteTodo(todo: Todo) {
        viewModelScope.launch {
            repository.deleteTodo(todo)
        }
    }
}
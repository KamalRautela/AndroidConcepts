package com.example.androidconcepts.hilt.module

import android.content.Context
import androidx.room.Room
import com.example.androidconcepts.hilt.room.AnimalDao
import com.example.androidconcepts.hilt.room.AnimalDatabase
import com.example.androidconcepts.room.TodoDao
import com.example.androidconcepts.room.TodoDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun providesDataBase(@ApplicationContext context: Context): AnimalDatabase {
        return Room.databaseBuilder(context, AnimalDatabase::class.java,"animal_database").build()
    }

    @Provides
    @Singleton
    fun providesDao(database: AnimalDatabase) : AnimalDao {
        return database.animalDao()
    }
}
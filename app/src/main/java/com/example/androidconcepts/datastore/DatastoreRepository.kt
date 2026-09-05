package com.example.androidconcepts.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.datastore by preferencesDataStore(name = "user_prefs")

class DatastoreRepository(private val datastore : DataStore<Preferences>) {
    companion object {
        val USERNAME_KEY = stringPreferencesKey("user_name")
        val IS_LOGGED_IN_KEY = booleanPreferencesKey("isLoggedIn")
    }

    val userName : Flow<String> = datastore.data.map { it[USERNAME_KEY] ?: "" }
    val isLoggedIn : Flow<Boolean> = datastore.data.map { it[IS_LOGGED_IN_KEY] ?: false }

    suspend fun saveData(userName : String,isLoggedIn : Boolean) {
        datastore.edit {
            it[USERNAME_KEY] = userName
            it[IS_LOGGED_IN_KEY] = isLoggedIn
        }
    }

    suspend fun clear() {
        datastore.edit { it.clear() }
    }
}
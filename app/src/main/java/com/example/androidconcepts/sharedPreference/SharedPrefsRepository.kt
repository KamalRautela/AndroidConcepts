package com.example.androidconcepts.sharedPreference

import android.content.SharedPreferences
import androidx.core.content.edit

class SharedPrefsRepository(
    private val prefs: SharedPreferences
) {
    fun saveUsername(username: String, isLoggedIn: Boolean) {
        prefs.edit {
            putString("username", username)
                .putBoolean("isLoggedIn", isLoggedIn)
        }
    }

    fun getUsername(): String = prefs.getString("username", "") ?: ""

    fun isLoggedIn(): Boolean = prefs.getBoolean("isLoggedIn", false)

    fun clear() {
        prefs.edit { clear() }
    }
}

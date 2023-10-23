package com.example.storydicodingapp.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.storydicodingapp.utils.SettingsPreferences.Companion.settingsPreferences
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = settingsPreferences)

class SettingsPreferences private constructor(private val dataStore: DataStore<Preferences>) {
    fun getPrefs() = dataStore.data.map { it[token] ?: preferencesDefaultValue }

    suspend fun savePrefs(
        userToken: String
    ) {
        dataStore.edit { prefs ->
            prefs[token] = userToken
        }
    }

    suspend fun clearPrefs() {
        dataStore.edit { prefs ->
            prefs.clear()
        }
    }

    companion object {
        const val settingsPreferences: String = "settings_preferences"
        const val preferencesDefaultValue: String = "preferences_default_value"

        val token = stringPreferencesKey("token")

        @Volatile
        private var INSTANCE: SettingsPreferences? = null

        fun getInstance(dataStore: DataStore<Preferences>) = INSTANCE ?: synchronized(this) {
            val instance = SettingsPreferences(dataStore)
            INSTANCE = instance
            instance
        }
    }
}
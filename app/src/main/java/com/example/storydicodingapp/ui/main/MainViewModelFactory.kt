package com.example.storydicodingapp.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.storydicodingapp.data.remote.ApiService
import com.example.storydicodingapp.data.repository.StoryRepository
import com.example.storydicodingapp.utils.SettingsPreferences

class MainViewModelFactory private constructor(
    private val settingsPreferences: SettingsPreferences,
    private val apiService: ApiService
) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(settingsPreferences, StoryRepository(apiService)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }

    companion object {
        @Volatile
        private var INSTANCE: MainViewModelFactory? = null

        @JvmStatic
        fun getInstance(
            settingsPreferences: SettingsPreferences,
            apiService: ApiService
        ): MainViewModelFactory {
            if (INSTANCE == null) {
                synchronized(MainViewModelFactory::class.java) {
                    INSTANCE = MainViewModelFactory(settingsPreferences, apiService)
                }
            }
            return INSTANCE as MainViewModelFactory
        }
    }
}
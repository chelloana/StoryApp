package com.example.storydicodingapp.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.storydicodingapp.utils.SettingsPreferences

class SplashViewModelFactory private constructor(
    private val settingsPreferences: SettingsPreferences
) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SplashViewModel::class.java)) {
            return SplashViewModel(settingsPreferences) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }

    companion object {
        @Volatile
        private var INSTANCE: SplashViewModelFactory? = null

        @JvmStatic
        fun getInstance(
            settingsPreferences: SettingsPreferences
        ): SplashViewModelFactory {
            if (INSTANCE == null) {
                synchronized(SplashViewModelFactory::class.java) {
                    INSTANCE = SplashViewModelFactory(
                        settingsPreferences
                    )
                }
            }
            return INSTANCE as SplashViewModelFactory
        }
    }
}
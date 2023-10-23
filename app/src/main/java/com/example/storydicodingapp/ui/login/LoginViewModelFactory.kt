package com.example.storydicodingapp.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.storydicodingapp.data.remote.ApiService
import com.example.storydicodingapp.data.repository.AuthenticationRepository
import com.example.storydicodingapp.utils.SettingsPreferences

class LoginViewModelFactory private constructor(
    private val settingsPreferences: SettingsPreferences,
    private val apiService: ApiService
) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(settingsPreferences, AuthenticationRepository(apiService)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }

    companion object {
        @Volatile
        private var INSTANCE: LoginViewModelFactory? = null

        @JvmStatic
        fun getInstance(
            settingsPreferences: SettingsPreferences,
            apiService: ApiService
        ): LoginViewModelFactory {
            if (INSTANCE == null) {
                synchronized(LoginViewModelFactory::class.java) {
                    INSTANCE = LoginViewModelFactory(
                        settingsPreferences,
                        apiService
                    )
                }
            }
            return INSTANCE as LoginViewModelFactory
        }
    }
}
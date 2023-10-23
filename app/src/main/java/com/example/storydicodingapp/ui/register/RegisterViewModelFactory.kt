package com.example.storydicodingapp.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.storydicodingapp.data.remote.ApiService
import com.example.storydicodingapp.data.repository.AuthenticationRepository

class RegisterViewModelFactory private constructor(
    private val apiService: ApiService
) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            return RegisterViewModel(AuthenticationRepository(apiService)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }

    companion object {
        @Volatile
        private var INSTANCE: RegisterViewModelFactory? = null

        @JvmStatic
        fun getInstance(apiService: ApiService): RegisterViewModelFactory {
            if (INSTANCE == null) {
                synchronized(RegisterViewModelFactory::class.java) {
                    INSTANCE = RegisterViewModelFactory(apiService)
                }
            }
            return INSTANCE as RegisterViewModelFactory
        }
    }
}
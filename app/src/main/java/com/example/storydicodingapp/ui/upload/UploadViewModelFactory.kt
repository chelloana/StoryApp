package com.example.storydicodingapp.ui.upload

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.storydicodingapp.data.remote.ApiService
import com.example.storydicodingapp.data.repository.StoryRepository

class UploadViewModelFactory private constructor(
    private val apiService: ApiService
) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UploadViewModel::class.java)) {
            return UploadViewModel(StoryRepository(apiService)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }

    companion object {
        @Volatile
        private var INSTANCE: UploadViewModelFactory? = null

        @JvmStatic
        fun getInstance(apiService: ApiService): UploadViewModelFactory {
            if (INSTANCE == null) {
                synchronized(UploadViewModelFactory::class.java) {
                    INSTANCE = UploadViewModelFactory(apiService)
                }
            }
            return INSTANCE as UploadViewModelFactory
        }
    }
}
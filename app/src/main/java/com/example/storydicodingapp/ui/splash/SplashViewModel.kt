package com.example.storydicodingapp.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.storydicodingapp.utils.SettingsPreferences

class SplashViewModel(private val settingsPreferences: SettingsPreferences) : ViewModel() {
    fun getPrefs() = settingsPreferences.getPrefs().asLiveData()
}
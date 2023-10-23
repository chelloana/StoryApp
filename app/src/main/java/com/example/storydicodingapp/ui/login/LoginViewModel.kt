package com.example.storydicodingapp.ui.login

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.storydicodingapp.data.repository.AuthenticationRepository
import com.example.storydicodingapp.utils.Event
import com.example.storydicodingapp.utils.SettingsPreferences
import kotlinx.coroutines.launch

class LoginViewModel(
    private val settingsPreferences: SettingsPreferences,
    private val authenticationRepository: AuthenticationRepository
) : ViewModel() {

    val isEmailError = MutableLiveData<Boolean>()
    val isPassError = MutableLiveData<Boolean>()
    val isLoading = MutableLiveData<Boolean>()
    val errorMessage = MutableLiveData<Event<String>>()

    val canLogin = MediatorLiveData<Boolean>().apply {
        var emailFlag = true
        var passFlag = true
        value = false
        addSource(isEmailError) { email ->
            email?.let {
                emailFlag = it
                value = !passFlag && !emailFlag
            }
        }
        addSource(isPassError) { password ->
            password?.let {
                passFlag = it
                value = !passFlag && !emailFlag
            }
        }
    }

    fun loginUser(email: String, password: String) =
        authenticationRepository.login(email, password)

    fun savePrefs(
        token: String,
    ) {
        viewModelScope.launch {
            settingsPreferences.savePrefs(token)
        }
    }

    fun getPrefs() = settingsPreferences.getPrefs().asLiveData()
}
package com.example.storydicodingapp.ui.register

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.storydicodingapp.data.repository.AuthenticationRepository
import com.example.storydicodingapp.utils.Event

class RegisterViewModel(private val authenticationRepository: AuthenticationRepository) :
    ViewModel() {

    val isUsernameError = MutableLiveData<Boolean>()
    val isEmailError = MutableLiveData<Boolean>()
    val isPassError = MutableLiveData<Boolean>()
    val isLoading = MutableLiveData<Boolean>()
    val errorText = MutableLiveData<Event<String>>()

    val canRegister = MediatorLiveData<Boolean>().apply {
        var nameFlag = true
        var emailFlag = true
        var passFlag = true
        value = false
        addSource(isUsernameError) { username ->
            username?.let {
                nameFlag = it
                value = !passFlag && !emailFlag && !nameFlag
            }
        }
        addSource(isEmailError) { email ->
            email?.let {
                emailFlag = it
                value = !passFlag && !emailFlag && !nameFlag
            }
        }
        addSource(isPassError) { password ->
            password?.let {
                passFlag = it
                value = !passFlag && !emailFlag && !nameFlag
            }
        }
    }

    fun register(name: String, email: String, password: String) =
        authenticationRepository.register(name, email, password)
}
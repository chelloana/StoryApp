package com.example.storydicodingapp.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.example.storydicodingapp.data.models.ListStoryItem
import com.example.storydicodingapp.data.repository.StoryRepository
import com.example.storydicodingapp.utils.Event
import com.example.storydicodingapp.utils.Result
import com.example.storydicodingapp.utils.SettingsPreferences
import kotlinx.coroutines.launch

class MainViewModel(
    private val settingsPreferences: SettingsPreferences,
    private val storyRepository: StoryRepository
) : ViewModel() {
    val isLoading = MutableLiveData<Boolean>()
    val storyList = MutableLiveData<ArrayList<ListStoryItem>>(arrayListOf())
    val errorText = MutableLiveData<Event<String>>()

    init {
        getStory()
    }

    fun getStory() {
        viewModelScope.launch {
            storyRepository.getStories().asFlow().collect { result ->
                when (result) {
                    is Result.Loading -> {
                        isLoading.postValue(true)
                    }

                    is Result.Success -> {
                        isLoading.postValue(false)
                        storyList.postValue(result.data.listStory as ArrayList<ListStoryItem>?)
                    }

                    is Result.Error -> {
                        isLoading.postValue(false)
                        errorText.postValue(Event(result.error))
                    }
                }
            }
        }
    }

    fun clearPrefs() {
        viewModelScope.launch {
            settingsPreferences.clearPrefs()
        }
    }
}
package com.example.storydicodingapp.ui.upload

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.storydicodingapp.data.repository.StoryRepository
import com.example.storydicodingapp.utils.Event
import java.io.File

class UploadViewModel(private val storyRepository: StoryRepository) : ViewModel() {
    val imageFile = MutableLiveData<File>(null)
    val descText = MutableLiveData<String>(null)
    val isLoading = MutableLiveData<Boolean>(null)
    val errorText = MutableLiveData<Event<String>>(null)

    val canUpload = MediatorLiveData<Boolean>().apply {
        var imageFlag = true
        var descFlag = true
        value = false
        addSource(imageFile) { x ->
            imageFlag = x == null
            value = !imageFlag && !descFlag
        }
        addSource(descText) { x ->
            descFlag = x.isNullOrEmpty()
            value = !imageFlag && !descFlag
        }
    }

    fun upload(
        file: File,
        description: String,
    ) = storyRepository.upload(
        file,
        description
    )
}
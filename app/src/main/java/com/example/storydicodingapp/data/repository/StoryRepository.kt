package com.example.storydicodingapp.data.repository

import androidx.lifecycle.liveData
import com.example.storydicodingapp.utils.Result
import com.example.storydicodingapp.data.remote.ApiService
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class StoryRepository(
    private val apiService: ApiService,
) {
    fun getStories() = liveData {
        emit(Result.Loading)
        try {
            val mappedStoriesResponse = apiService.getStories(50)
            emit(Result.Success(mappedStoriesResponse))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun upload(
        image: File,
        desc: String
    ) = liveData {
        emit(Result.Loading)
        try {
            val uploadResponse = apiService.upload(
                MultipartBody.Part.createFormData(
                    "photo",
                    image.name,
                    image.asRequestBody("image/jpeg".toMediaTypeOrNull())
                ),
                desc.toRequestBody("text/plain".toMediaType())
            )

            emit(Result.Success(uploadResponse))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }
}
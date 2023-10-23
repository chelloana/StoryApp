package com.example.storydicodingapp.data.remote

import com.example.storydicodingapp.data.models.Login
import com.example.storydicodingapp.data.models.Register
import com.example.storydicodingapp.data.models.Stories
import com.example.storydicodingapp.data.models.Upload
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiService {
    @POST("login")
    @FormUrlEncoded
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Login

    @POST("register")
    @FormUrlEncoded
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Register

    @Multipart
    @POST("stories")
    suspend fun upload(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody
    ): Upload

    @GET("stories")
    suspend fun getStories(
        @Query("size") size: Int
    ): Stories
}
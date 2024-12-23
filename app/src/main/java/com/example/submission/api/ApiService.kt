package com.example.submission.api

import com.example.submission.response.LoginResponse
import com.example.submission.response.RegisterResponse
import com.example.submission.response.StoryDetailResponse
import com.example.submission.response.StoryResponse
import com.example.submission.response.UploadResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): RegisterResponse

    // Endpoint Login
    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @GET("stories")
    suspend fun getStories(): Response<StoryResponse>

    @GET("stories")
    suspend fun getStoriesWithLocation(
        @Query("location") location: Int = 1
    ): StoryResponse

    @GET("stories")
    suspend fun getStories(
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 20
    ): StoryResponse

    @GET("stories/{id}")
    suspend fun getDetailStories(
        @Path("id") storyId: String
    ): Response<StoryDetailResponse>

    @Multipart
    @POST("stories")
    suspend fun postStories(
        @Part("description") description: RequestBody,
        @Part photo: MultipartBody.Part,
        @Part("lat") lat: RequestBody? = null,
        @Part("lon") lon: RequestBody? = null
    ): Response<UploadResponse>
}
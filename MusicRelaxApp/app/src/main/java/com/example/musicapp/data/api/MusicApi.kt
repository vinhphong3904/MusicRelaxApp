package com.example.musicapp.data.api

import com.example.musicapp.data.model.home.RecommendSongsResponse
import com.example.musicapp.data.model.home.TopSongsResponse
import com.example.musicapp.data.model.login.LoginRequest
import com.example.musicapp.data.model.login.LoginResponse
import com.example.musicapp.data.model.register.RegisterRequest
import com.example.musicapp.data.model.register.RegisterResponse
import com.example.musicapp.data.model.home.MeResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface MusicApi {

    @POST("register")
    suspend fun register(
        @Body request: RegisterRequest
    ): RegisterResponse

    @POST("login")
    suspend fun login(
        @Body request: LoginRequest
    ): LoginResponse

    @GET("songs/top")
    suspend fun getTopSongs(): TopSongsResponse

    @GET("songs/recommend")
    suspend fun getRecommendSongs(): RecommendSongsResponse

    @GET("me")
    suspend fun getMe(): MeResponse
}

package com.example.musicapp.data.network

import com.example.musicapp.data.api.AuthInterceptor
import com.example.musicapp.data.service.TokenManager
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

object OkHttpProvider {

    fun provide(): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(
                AuthInterceptor())
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
}

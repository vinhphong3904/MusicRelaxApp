package com.example.musicapp.data.api

import com.example.musicapp.data.network.OkHttpProvider
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL = "http://192.168.43.1:8080/api/"
    //private const val BASE_URL = "http://10.0.2.2:8080/api/"

    val musicApi: MusicApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(OkHttpProvider.provide())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MusicApi::class.java)
    }
}

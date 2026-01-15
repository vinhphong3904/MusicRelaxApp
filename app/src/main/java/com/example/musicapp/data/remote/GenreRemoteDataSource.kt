package com.example.musicapp.data.remote

import com.example.musicapp.core.network.ApiService
import com.example.musicapp.data.model.dto.*
import javax.inject.Inject

class GenreRemoteDataSource @Inject constructor(private val api: ApiService) {
    suspend fun fetchGenres(token: String): GenresResponse {
        return api.getGenres("Bearer $token")
    }

    suspend fun fetchGenreDetail(token: String, id: Int): GenreDetailResponse {
        return api.getGenreById("Bearer $token", id)
    }
}

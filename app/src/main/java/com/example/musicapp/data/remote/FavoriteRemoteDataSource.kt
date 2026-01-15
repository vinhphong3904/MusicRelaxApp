package com.example.musicapp.data.remote

import com.example.musicapp.core.network.ApiService
import com.example.musicapp.data.model.dto.*
import javax.inject.Inject

class FavoriteRemoteDataSource @Inject constructor(private val api: ApiService) {
    suspend fun fetchFavorites(token: String): FavoritesResponse {
        return api.getFavorites("Bearer $token")
    }

    suspend fun addFavorite(token: String, songId: Int): SimpleResponse {
        return api.addFavorite("Bearer $token", AddFavoriteRequest(songId))
    }

    suspend fun removeFavorite(token: String, songId: Int): SimpleResponse {
        return api.removeFavorite("Bearer $token", songId)
    }
}

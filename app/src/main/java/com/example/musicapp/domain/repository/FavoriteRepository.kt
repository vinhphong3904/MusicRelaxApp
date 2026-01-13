package com.example.musicapp.data.repository

import com.example.musicapp.data.remote.FavoriteRemoteDataSource
import com.example.musicapp.data.model.dto.FavoriteDto

interface FavoriteRepositoryInterface {
    suspend fun getFavorites(token: String): List<FavoriteDto>
    suspend fun addFavorite(token: String, songId: Int): String
    suspend fun removeFavorite(token: String, songId: Int): String
}

class FavoriteRepository(
    private val remote: FavoriteRemoteDataSource
) : FavoriteRepositoryInterface {
    override suspend fun getFavorites(token: String): List<FavoriteDto> {
        return remote.fetchFavorites(token).data
    }

    override suspend fun addFavorite(token: String, songId: Int): String {
        return remote.addFavorite(token, songId).message
    }

    override suspend fun removeFavorite(token: String, songId: Int): String {
        return remote.removeFavorite(token, songId).message
    }
}

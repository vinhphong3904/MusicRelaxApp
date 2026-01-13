package com.example.musicapp.data.remote

import com.example.musicapp.core.network.ApiService
import com.example.musicapp.data.model.dto.*

class AlbumRemoteDataSource(private val api: ApiService) {

    suspend fun fetchAlbums(
        token: String,
        keyword: String? = null,
        artistId: Int? = null,
        page: Int = 1,
        limit: Int = 20
    ): AlbumsResponse {
        return api.getAlbums("Bearer $token", keyword, artistId, page, limit)
    }

    suspend fun fetchAlbumDetail(token: String, id: Int): AlbumDetailResponse {
        return api.getAlbumById("Bearer $token", id)
    }
}

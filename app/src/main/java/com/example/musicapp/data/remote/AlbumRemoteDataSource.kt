package com.example.musicapp.data.remote

import com.example.musicapp.core.network.ApiService
import com.example.musicapp.data.model.dto.*
import javax.inject.Inject

class AlbumRemoteDataSource @Inject constructor(
    private val api: ApiService
) {
    suspend fun fetchAlbums(
        token: String,
        keyword: String? = null,
        artistId: Int? = null,
        page: Int = 1,
        limit: Int = 20
    ): List<AlbumDto> {
        val response = api.getAlbums(token, keyword, artistId, page, limit)
        return response.data // lấy danh sách AlbumDto
    }

    suspend fun fetchAlbumDetail(token: String, id: Int): AlbumDto {
        val response = api.getAlbumById(token, id)
        return response.data // lấy AlbumDto
    }
}

package com.example.musicapp.data.remote

import com.example.musicapp.core.network.ApiService
import com.example.musicapp.data.model.dto.SongsResponse
import com.example.musicapp.data.model.dto.SongDetailResponse

class SongRemoteDataSource(
    private val api: ApiService
) {
    suspend fun fetchSongs(
        keyword: String? = null,
        genreId: Int? = null,
        artistId: Int? = null,
        page: Int = 1,
        limit: Int = 20
    ): SongsResponse {
        return api.getSongs(keyword, genreId, artistId, page, limit)
    }

    suspend fun fetchSongDetail(id: Int): SongDetailResponse {
        return api.getSongDetail(id)
    }
}

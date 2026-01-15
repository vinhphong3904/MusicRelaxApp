package com.example.musicapp.data.remote

import com.example.musicapp.core.network.ApiService
import com.example.musicapp.data.model.dto.SongsResponse
import com.example.musicapp.data.model.dto.SongDetailResponse
import com.example.musicapp.data.model.dto.SongsSimpleResponse

class SongRemoteDataSource(
    private val api: ApiService
) {
    suspend fun fetchSongs(
        keyword: String? = null,
        genreId: Int? = null,
        artistId: Int? = null,
        page: Int = 1,
        limit: Int = 20
    ) = api.getSongs(keyword, genreId, artistId, page, limit)

    suspend fun fetchSongDetail(id: Int) = api.getSongDetail(id)

    suspend fun fetchTopSongs() = api.getTopSongs()

    suspend fun fetchRecommendSongs(token: String) = api.getRecommendSongs("Bearer $token")
}


package com.example.musicapp.data.remote

import com.example.musicapp.data.model.dto.*
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import javax.inject.Inject

interface MusicApiService {
    @GET("songs")
    suspend fun getSongs(
        @Query("page") page: Int,
        @Query("limit") limit: Int
    ): SongsResponse

    @GET("songs/{id}")
    suspend fun getSongDetail(@Path("id") id: Int): SongDetailResponse

    @GET("songs/top")
    suspend fun getTopSongs(): SongsSimpleResponse
}

class MusicRemoteDataSource @Inject constructor(
    private val apiService: MusicApiService
) {
    suspend fun getSongs(page: Int, limit: Int): SongsResponse {
        return apiService.getSongs(page, limit)
    }

    suspend fun getSongDetail(songId: Int): SongDetailResponse {
        return apiService.getSongDetail(songId)
    }

    suspend fun getTopSongs(): SongsSimpleResponse {
        return apiService.getTopSongs()
    }
}
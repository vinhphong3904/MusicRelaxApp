package com.example.musicapp.data.remote

import com.example.musicapp.core.network.ApiService
import com.example.musicapp.data.model.dto.*
import javax.inject.Inject

class ArtistRemoteDataSource @Inject constructor(private val api: ApiService) {

    suspend fun fetchArtists(token: String, keyword: String? = null): ArtistsResponse {
        return api.getArtists("Bearer $token", keyword)
    }

    suspend fun fetchArtistDetail(token: String, id: Int): ArtistDetailResponse {
        return api.getArtistById("Bearer $token", id)
    }
}

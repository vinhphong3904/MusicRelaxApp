package com.example.musicapp.data.remote

import com.example.musicapp.core.network.ApiService
import com.example.musicapp.data.model.dto.SearchResponse
import javax.inject.Inject

class SearchRemoteDataSource @Inject constructor(private val api: ApiService) {
    suspend fun search(token: String, keyword: String, page: Int = 1, limit: Int = 10): SearchResponse {
        return api.search("Bearer $token", keyword, page, limit)
    }
}

package com.example.musicapp.data.remote

import com.example.musicapp.core.network.ApiService
import com.example.musicapp.data.model.dto.*
import javax.inject.Inject

class HistoryRemoteDataSource @Inject constructor(private val api: ApiService) {
    suspend fun fetchHistories(token: String, limit: Int = 50) = api.getHistories("Bearer $token", limit)
    suspend fun addHistory(token: String, request: AddHistoryRequest) = api.addHistory("Bearer $token", request)
}

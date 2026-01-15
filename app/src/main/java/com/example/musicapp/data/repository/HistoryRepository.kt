package com.example.musicapp.data.repository

import com.example.musicapp.data.model.dto.*
import com.example.musicapp.data.remote.HistoryRemoteDataSource
import javax.inject.Inject

interface HistoryRepositoryInterface {
    suspend fun getHistories(token: String, limit: Int = 50): List<HistoryDto>
    suspend fun addHistory(token: String, request: AddHistoryRequest): Boolean
}

class HistoryRepository @Inject constructor(private val remoteDataSource: HistoryRemoteDataSource) : HistoryRepositoryInterface {
    override suspend fun getHistories(token: String, limit: Int) = remoteDataSource.fetchHistories(token, limit).data
    override suspend fun addHistory(token: String, request: AddHistoryRequest) = remoteDataSource.addHistory(token, request).success
}

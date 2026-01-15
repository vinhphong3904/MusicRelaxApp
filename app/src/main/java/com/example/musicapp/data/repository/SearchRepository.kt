package com.example.musicapp.data.repository
import com.example.musicapp.data.remote.SearchRemoteDataSource
import com.example.musicapp.data.model.dto.SearchResponse
import javax.inject.Inject

interface SearchRepositoryInterface {
    suspend fun search(token: String, keyword: String, page: Int = 1, limit: Int = 10): SearchResponse
}

class SearchRepository @Inject constructor(private val remoteDataSource: SearchRemoteDataSource) : SearchRepositoryInterface {
    override suspend fun search(token: String, keyword: String, page: Int, limit: Int): SearchResponse {
        return remoteDataSource.search(token, keyword, page, limit)
    }
}

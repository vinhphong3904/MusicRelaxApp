package com.example.musicapp.domain.repository
import com.example.musicapp.data.model.dto.SearchResponse
import com.example.musicapp.data.remote.SearchRemoteDataSource
import com.example.musicapp.data.model.dto.*

interface SearchRepositoryInterface {
    suspend fun search(token: String, keyword: String, page: Int = 1, limit: Int = 10): SearchResponse
}

class SearchRepository(private val remote: SearchRemoteDataSource) : SearchRepositoryInterface {
    override suspend fun search(token: String, keyword: String, page: Int, limit: Int): SearchResponse {
        return remote.search(token, keyword, page, limit)
    }
}

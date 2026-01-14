package com.example.musicapp.domain.usecase

import com.example.musicapp.data.repository.SearchRepositoryInterface
import com.example.musicapp.data.model.dto.*


class SearchUseCase(private val repository: SearchRepositoryInterface) {
    suspend operator fun invoke(token: String, keyword: String, page: Int = 1, limit: Int = 10): SearchResponse {
        return repository.search(token, keyword, page, limit)
    }
}

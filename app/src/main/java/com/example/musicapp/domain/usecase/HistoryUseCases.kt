package com.example.musicapp.domain.usecase

import com.example.musicapp.data.repository.HistoryRepositoryInterface
import com.example.musicapp.data.model.dto.HistoryDto

class GetHistoriesUseCase(private val repo: HistoryRepositoryInterface) {
    suspend operator fun invoke(token: String, limit: Int = 50) = repo.getHistories(token, limit)
}

class AddHistoryUseCase(private val repo: HistoryRepositoryInterface) {
    suspend operator fun invoke(token: String, request: AddHistoryRequest) = repo.addHistory(token, request)
}

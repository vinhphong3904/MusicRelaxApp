package com.example.musicapp.domain.usecase

import com.example.musicapp.data.repository.FavoriteRepositoryInterface
import com.example.musicapp.data.model.dto.FavoriteDto

class GetFavoritesUseCase(private val repo: FavoriteRepositoryInterface) {
    suspend operator fun invoke(token: String): List<FavoriteDto> {
        return repo.getFavorites(token)
    }
}

class AddFavoriteUseCase(private val repo: FavoriteRepositoryInterface) {
    suspend operator fun invoke(token: String, songId: Int): String {
        return repo.addFavorite(token, songId)
    }
}

class RemoveFavoriteUseCase(private val repo: FavoriteRepositoryInterface) {
    suspend operator fun invoke(token: String, songId: Int): String {
        return repo.removeFavorite(token, songId)
    }
}

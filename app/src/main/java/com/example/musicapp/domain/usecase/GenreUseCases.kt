package com.example.musicapp.domain.usecase

import com.example.musicapp.data.repository.GenreRepositoryInterface
import com.example.musicapp.domain.model.Genre

class GetGenresUseCase(private val repo: GenreRepositoryInterface) {
    suspend operator fun invoke(token: String): List<Genre> {
        return repo.getGenres(token)
    }
}

class GetGenreDetailUseCase(private val repo: GenreRepositoryInterface) {
    suspend operator fun invoke(token: String, id: Int): Genre {
        return repo.getGenreDetail(token, id)
    }
}

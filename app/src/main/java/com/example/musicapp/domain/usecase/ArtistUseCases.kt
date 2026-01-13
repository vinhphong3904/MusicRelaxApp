package com.example.musicapp.domain.usecase

import com.example.musicapp.data.repository.ArtistRepositoryInterface
import com.example.musicapp.domain.model.Artist

class GetArtistsUseCase(private val repository: ArtistRepositoryInterface) {
    suspend operator fun invoke(token: String, keyword: String? = null): List<Artist> {
        return repository.getArtists(token, keyword)
    }
}

class GetArtistDetailUseCase(private val repository: ArtistRepositoryInterface) {
    suspend operator fun invoke(token: String, id: Int): Artist {
        return repository.getArtistDetail(token, id)
    }
}

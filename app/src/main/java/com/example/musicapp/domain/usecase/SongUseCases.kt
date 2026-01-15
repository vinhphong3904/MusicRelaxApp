package com.example.musicapp.domain.usecase

import com.example.musicapp.data.repository.SongRepositoryInterface
import com.example.musicapp.data.model.dto.SongDto
import com.example.musicapp.data.model.dto.SongDetailDto
import com.example.musicapp.data.model.dto.SongTopDto

class GetSongsUseCase(
    private val repository: SongRepositoryInterface
) {
    suspend operator fun invoke(
        keyword: String? = null,
        genreId: Int? = null,
        artistId: Int? = null,
        page: Int = 1,
        limit: Int = 20
    ): List<SongDto> {
        return repository.getSongs(keyword, genreId, artistId, page, limit)
    }
}

class GetSongDetailUseCase(
    private val repository: SongRepositoryInterface
) {
    suspend operator fun invoke(id: Int): SongDetailDto {
        return repository.getSongDetail(id)
    }
}

class GetTopSongsUseCase(
    private val repository: SongRepositoryInterface
) {
    suspend operator fun invoke(): List<SongTopDto> {
        return repository.getTopSongs()
    }
}

class GetRecommendSongsUseCase(
    private val repository: SongRepositoryInterface
) {
    suspend operator fun invoke(): List<SongTopDto> {
        return repository.getRecommendSongs()
    }
}
package com.example.musicapp.domain.usecase

import com.example.musicapp.data.repository.SongRepositoryInterface
import com.example.musicapp.domain.model.Song
import com.example.musicapp.domain.model.SongDetail

class GetSongsUseCase(
    private val repository: SongRepositoryInterface
) {
    suspend operator fun invoke(
        keyword: String? = null,
        genreId: Int? = null,
        artistId: Int? = null,
        page: Int = 1,
        limit: Int = 20
    ): List<Song> {
        return repository.getSongs(keyword, genreId, artistId, page, limit)
    }
}

class GetSongDetailUseCase(
    private val repository: SongRepositoryInterface
) {
    suspend operator fun invoke(id: Int): SongDetail {
        return repository.getSongDetail(id)
    }
}

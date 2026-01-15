package com.example.musicapp.domain.usecase

import com.example.musicapp.domain.repository.MusicRepository


data class SongUseCases(
    val getSongs: GetSongsUseCase,
    val getSongDetail: GetSongDetailUseCase,
    val getTopSongs: GetTopSongsUseCase,
    val getRecommendSongs: GetRecommendSongsUseCase
)

class GetSongsUseCase(private val repository: MusicRepository) {
    suspend operator fun invoke(page: Int=1, limit: Int=10) =
        repository.getSongs(page, limit)
}

class GetSongDetailUseCase(private val repository: MusicRepository) {
    suspend operator fun invoke(songId: Int) =
        repository.getSongDetail(songId)
}

class GetTopSongsUseCase(private val repository: MusicRepository) {
    suspend operator fun invoke() =
        repository.getTopSongs()
}

class GetRecommendSongsUseCase(private val repository: MusicRepository) {
    suspend operator fun invoke() =
        repository.getRecommendSongs()
}

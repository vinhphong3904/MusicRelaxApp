package com.example.musicapp.domain.usecase

import com.example.musicapp.domain.model.Song
import com.example.musicapp.domain.repository.MusicRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSongsUseCase @Inject constructor(
    private val repository: MusicRepository
) {
    suspend operator fun invoke(page: Int = 1, limit: Int = 20): Flow<Result<List<Song>>> {
        return repository.getSongs(page, limit)
    }
}

class GetSongDetailUseCase @Inject constructor(
    private val repository: MusicRepository
) {
    suspend operator fun invoke(songId: Int): Flow<Result<Song>> {
        return repository.getSongDetail(songId)
    }
}
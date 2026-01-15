package com.example.musicapp.domain.repository

import com.example.musicapp.domain.model.Song
import com.example.musicapp.domain.model.SongDetail
import kotlinx.coroutines.flow.Flow


interface MusicRepository {
    suspend fun getSongs(page: Int=1, limit: Int=10): Flow<Result<List<Song>>>
    suspend fun getSongDetail(id: Int): Flow<Result<SongDetail>>
    suspend fun getTopSongs(): Flow<Result<List<Song>>>
    suspend fun getRecommendSongs(): Flow<Result<List<Song>>>
}

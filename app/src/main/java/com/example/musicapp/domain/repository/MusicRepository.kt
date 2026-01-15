package com.example.musicapp.domain.repository

import com.example.musicapp.domain.model.Song
import kotlinx.coroutines.flow.Flow

interface MusicRepository {
    suspend fun getSongs(page: Int, limit: Int): Flow<Result<List<Song>>>
    suspend fun getSongDetail(songId: Int): Flow<Result<Song>>
    suspend fun getTopSongs(): Flow<Result<List<Song>>>
}
package com.example.musicapp.data.repository

import com.example.musicapp.data.mapper.toDomain
import com.example.musicapp.data.remote.SongRemoteDataSource
import com.example.musicapp.domain.model.Song
import com.example.musicapp.domain.model.SongDetail
import com.example.musicapp.domain.repository.MusicRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SongRepositoryImpl @Inject constructor(
    private val remote: SongRemoteDataSource
) : MusicRepository {

    override suspend fun getSongs(page: Int, limit: Int): Flow<Result<List<Song>>> = flow {
        try {
            val dtoList = remote.fetchSongs(null, null, null, page, limit).data
            emit(Result.success(dtoList.map { it.toDomain() }))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override suspend fun getSongDetail(id: Int): Flow<Result<SongDetail>> = flow {
        try {
            val dto = remote.fetchSongDetail(id).data
            emit(Result.success(dto.toDomain()))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override suspend fun getTopSongs(): Flow<Result<List<Song>>> = flow {
        try {
            val dtoList = remote.fetchTopSongs().data
            emit(Result.success(dtoList.map { it.toDomain() }))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override suspend fun getRecommendSongs(): Flow<Result<List<Song>>> = flow {
        try {
            val dtoList = remote.fetchRecommendSongs().data
            emit(Result.success(dtoList.map { it.toDomain() }))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}

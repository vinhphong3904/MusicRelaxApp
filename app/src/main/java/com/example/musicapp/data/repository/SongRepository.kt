package com.example.musicapp.data.repository

import com.example.musicapp.data.model.dto.SongDetailDto
import com.example.musicapp.data.model.dto.SongDto
import com.example.musicapp.data.model.dto.SongTopDto
import com.example.musicapp.data.remote.SongRemoteDataSource
import com.example.musicapp.domain.model.Song
import com.example.musicapp.domain.model.SongDetail
import javax.inject.Inject

interface SongRepositoryInterface {
    suspend fun getSongs(
        keyword: String? = null,
        genreId: Int? = null,
        artistId: Int? = null,
        page: Int = 1,
        limit: Int = 20
    ): List<SongDto>

    suspend fun getSongDetail(id: Int): SongDetailDto

    suspend fun getTopSongs(): List<SongTopDto>

    suspend fun getRecommendSongs(): List<SongTopDto>
}


class SongRepository @Inject constructor(
    private val remote: SongRemoteDataSource
) : SongRepositoryInterface {

    override suspend fun getSongs(
        keyword: String?,
        genreId: Int?,
        artistId: Int?,
        page: Int,
        limit: Int
    ): List<SongDto> {
        return remote.fetchSongs(keyword, genreId, artistId, page, limit).data
    }

    override suspend fun getSongDetail(id: Int): SongDetailDto {
        return remote.fetchSongDetail(id).data
    }

    override suspend fun getTopSongs(): List<SongTopDto> {
        return remote.fetchTopSongs().data
    }

    override suspend fun getRecommendSongs(): List<SongTopDto> {
        return remote.fetchRecommendSongs().data
    }
}


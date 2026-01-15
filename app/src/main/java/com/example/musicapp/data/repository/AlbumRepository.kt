package com.example.musicapp.data.repository

import com.example.musicapp.data.model.dto.AlbumDetailResponse
import com.example.musicapp.data.model.dto.AlbumsResponse
import com.example.musicapp.data.model.dto.toDomain
import com.example.musicapp.data.remote.AlbumRemoteDataSource
import com.example.musicapp.domain.model.Album
import javax.inject.Inject

interface AlbumRepositoryInterface {
    suspend fun getAlbums(
        token: String,
        keyword: String? = null,
        artistId: Int? = null,
        page: Int = 1,
        limit: Int = 20
    ): List<Album>

    suspend fun getAlbumDetail(token: String, id: Int): Album
}

class AlbumRepository @Inject constructor(
    private val remoteDataSource: AlbumRemoteDataSource
) : AlbumRepositoryInterface {

    override suspend fun getAlbums(
        token: String,
        keyword: String?,
        artistId: Int?,
        page: Int,
        limit: Int
    ): List<Album> {
        val dtos = remoteDataSource.fetchAlbums(token, keyword, artistId, page, limit)
        return dtos.map { it.toDomain() }
    }

    override suspend fun getAlbumDetail(token: String, id: Int): Album {
        val dto = remoteDataSource.fetchAlbumDetail(token, id)
        return dto.toDomain()
    }
}




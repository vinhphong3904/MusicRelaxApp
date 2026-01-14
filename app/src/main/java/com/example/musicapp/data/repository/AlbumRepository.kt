package com.example.musicapp.data.repository

import com.example.musicapp.data.remote.AlbumRemoteDataSource
import com.example.musicapp.domain.model.Album

interface AlbumRepositoryInterface {
    suspend fun getAlbums(token: String, keyword: String? = null, artistId: Int? = null, page: Int = 1, limit: Int = 20): List<Album>
    suspend fun getAlbumDetail(token: String, id: Int): Album
}

class AlbumRepository(
    private val remoteDataSource: AlbumRemoteDataSource
) : AlbumRepositoryInterface {
    override suspend fun getAlbums(token: String, keyword: String?, artistId: Int?, page: Int, limit: Int): List<Album> {
        val response = remoteDataSource.fetchAlbums(token, keyword, artistId, page, limit)
        return response.data.map {
            Album(
                id = it.id,
                title = it.title,
                releaseDate = it.release_date,
                description = it.description,
                coverImageUrl = it.cover_image_url,
                createdAt = it.created_at,
                artistId = it.artist_id,
                artistName = it.artist_name
            )
        }
    }

    override suspend fun getAlbumDetail(token: String, id: Int): Album {
        val response = remoteDataSource.fetchAlbumDetail(token, id)
        val it = response.data
        return Album(
            id = it.id,
            title = it.title,
            releaseDate = it.release_date,
            description = it.description,
            coverImageUrl = it.cover_image_url,
            createdAt = it.created_at,
            artistId = it.artist_id,
            artistName = it.artist_name
        )
    }
}

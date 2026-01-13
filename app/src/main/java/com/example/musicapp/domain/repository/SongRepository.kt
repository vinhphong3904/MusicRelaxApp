package com.example.musicapp.data.repository

import com.example.musicapp.data.remote.SongRemoteDataSource
import com.example.musicapp.domain.model.Song

interface SongRepositoryInterface {
    suspend fun getSongs(
        keyword: String? = null,
        genreId: Int? = null,
        artistId: Int? = null,
        page: Int = 1,
        limit: Int = 20
    ): List<Song>

    suspend fun getSongDetail(id: Int): Song
}

class SongRepository(
    private val remoteDataSource: SongRemoteDataSource
) : SongRepositoryInterface {

    override suspend fun getSongs(
        keyword: String?,
        genreId: Int?,
        artistId: Int?,
        page: Int,
        limit: Int
    ): List<Song> {
        val response = remoteDataSource.fetchSongs(keyword, genreId, artistId, page, limit)
        return response.data.map {
            Song(
                id = it.id,
                title = it.title,
                duration = it.duration,
                src = it.src,
                coverImageUrl = it.cover_image_url,
                viewCount = it.view_count,
                slug = it.slug,
                artistId = it.artist_id,
                artistName = it.artist_name,
                genreId = it.genre_id,
                genreName = it.genre_name
            )
        }
    }

    override suspend fun getSongDetail(id: Int): Song {
        val response = remoteDataSource.fetchSongDetail(id)
        val it = response.data
        return Song(
            id = it.id,
            title = it.title,
            duration = it.duration,
            src = it.src,
            coverImageUrl = it.cover_image_url,
            viewCount = it.view_count,
            slug = it.slug,
            artistId = it.artist_id,
            artistName = it.artist_name,
            genreId = it.genre_id,
            genreName = it.genre_name
        )
    }
}

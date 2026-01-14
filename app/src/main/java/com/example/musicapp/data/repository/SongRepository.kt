package com.example.musicapp.data.repository

import com.example.musicapp.data.remote.SongRemoteDataSource
import com.example.musicapp.domain.model.Song
import com.example.musicapp.domain.model.SongDetail

interface SongRepositoryInterface {
    suspend fun getSongs(
        keyword: String? = null,
        genreId: Int? = null,
        artistId: Int? = null,
        page: Int = 1,
        limit: Int = 20
    ): List<Song>

    suspend fun getSongDetail(id: Int): SongDetail
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
        return response.data.map { dto ->
            Song(
                id = dto.id,
                title = dto.title,
                duration = dto.duration,
                src = dto.src,
                coverImageUrl = dto.cover_image_url,
                viewCount = dto.view_count,
                slug = dto.slug,
                artistId = dto.artist_id,
                artistName = dto.artist_name,
                genreId = dto.genre_id,
                genreName = dto.genre_name
            )
        }
    }

    override suspend fun getSongDetail(id: Int): SongDetail {
        val response = remoteDataSource.fetchSongDetail(id)
        val dto = response.data
        return SongDetail(
            id = dto.id,
            title = dto.title,
            src = dto.src,
            duration = dto.duration,
            coverImageUrl = dto.cover_image_url,
            lyricsContent = dto.lyrics_content,
            viewCount = dto.view_count,
            slug = dto.slug,
            artistId = dto.artist_id,
            artistName = dto.artist_name,
            artistImage = dto.artist_image,
            genreId = dto.genre_id,
            genreName = dto.genre_name,
            albumId = dto.album_id,
            albumTitle = dto.album_title,
            albumCover = dto.album_cover
        )
    }
}

package com.example.musicapp.data.repository

import com.example.musicapp.data.remote.MusicRemoteDataSource
import com.example.musicapp.domain.model.Song
import com.example.musicapp.domain.repository.MusicRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MusicRepositoryImpl @Inject constructor(
    private val remoteDataSource: MusicRemoteDataSource
) : MusicRepository {

    override suspend fun getSongs(page: Int, limit: Int): Flow<Result<List<Song>>> = flow {
        try {
            val response = remoteDataSource.getSongs(page, limit)
            if (response.success) {
                val songs = response.data.map { dto ->
                    Song(
                        id = dto.id,
                        title = dto.title,
                        artistName = dto.artist_name,
                        duration = dto.duration,
                        audioUrl = dto.src,
                        coverImageUrl = dto.cover_image_url,
                        viewCount = dto.view_count,
                        slug = dto.slug,
                        artistId = dto.artist_id,
                        genreId = dto.genre_id,
                        genreName = dto.genre_name
                    )
                }
                emit(Result.success(songs))
            } else {
                emit(Result.failure(Exception("Failed to fetch songs")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override suspend fun getSongDetail(songId: Int): Flow<Result<Song>> = flow {
        try {
            val response = remoteDataSource.getSongDetail(songId)
            if (response.success) {
                val dto = response.data
                val song = Song(
                    id = dto.id,
                    title = dto.title,
                    artistName = dto.artist_name,
                    duration = dto.duration,
                    audioUrl = dto.src,
                    coverImageUrl = dto.cover_image_url,
                    viewCount = dto.view_count,
                    slug = dto.slug,
                    artistId = dto.artist_id,
                    genreId = dto.genre_id,
                    genreName = dto.genre_name,
                    lyricsContent = dto.lyrics_content,
                    albumId = dto.album_id,
                    albumTitle = dto.album_title,
                    albumCover = dto.album_cover
                )
                emit(Result.success(song))
            } else {
                emit(Result.failure(Exception("Failed to fetch song detail")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override suspend fun getTopSongs(): Flow<Result<List<Song>>> = flow {
        try {
            val response = remoteDataSource.getTopSongs()
            if (response.success) {
                val songs = response.data.map { dto ->
                    Song(
                        id = dto.id,
                        title = dto.title,
                        artistName = "",
                        duration = dto.duration_seconds,
                        audioUrl = dto.audio_url,
                        coverImageUrl = dto.cover_image_url,
                        viewCount = dto.view_count.toInt(),
                        slug = dto.slug,
                        artistId = dto.artist_id,
                        genreId = dto.genre_id ?: 0,
                        genreName = ""
                    )
                }
                emit(Result.success(songs))
            } else {
                emit(Result.failure(Exception("Failed to fetch top songs")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}
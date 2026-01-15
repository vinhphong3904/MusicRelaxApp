package com.example.musicapp.data.mapper

import com.example.musicapp.data.model.dto.SongDetailDto
import com.example.musicapp.data.model.dto.SongDto
import com.example.musicapp.data.model.dto.*
import com.example.musicapp.domain.model.Song

fun SongDto.toDomain(): Song {
    return Song(
        id = id,
        title = title,
        artistName = artist_name,
        duration = duration,
        audioUrl = src,
        coverImageUrl = cover_image_url,
        viewCount = view_count,
        slug = slug,
        artistId = artist_id,
        genreId = genre_id,
        genreName = genre_name
    )
}
fun SongDetailDto.toDomain(): Song {
    return Song(
        id = id,
        title = title,
        artistName = artist_name,
        duration = duration,
        audioUrl = src,
        coverImageUrl = cover_image_url,
        viewCount = view_count,
        slug = slug,
        artistId = artist_id,
        genreId = genre_id,
        genreName = genre_name,
        lyricsContent = lyrics_content,
        albumId = album_id,
        albumTitle = album_title,
        albumCover = album_cover
    )
}
fun SongTopDto.toDomain(): Song {
    return Song(
        id = id,
        title = title,
        artistName = "", // API top không trả artist name
        duration = duration_seconds,
        audioUrl = audio_url,
        coverImageUrl = cover_image_url,
        viewCount = view_count.toInt(),
        slug = slug,
        artistId = artist_id,
        genreId = genre_id ?: 0,
        genreName = "",
        albumId = album_id
    )
}

fun List<SongDto>.toDomain(): List<Song> =
    map { it.toDomain() }

fun List<SongTopDto>.toDomainTop(): List<Song> =
    map { it.toDomain() }


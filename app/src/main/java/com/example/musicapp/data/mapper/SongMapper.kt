package com.example.musicapp.data.mapper

import com.example.musicapp.data.model.dto.SongDto
import com.example.musicapp.data.model.dto.SongDetailDto
import com.example.musicapp.data.model.dto.SongTopDto
import com.example.musicapp.domain.model.Song
import com.example.musicapp.domain.model.SongDetail

fun Song.toDto(): SongDto = SongDto(
    id = id,
    title = title,
    duration = duration,
    cover_image_url = coverImageUrl,
    view_count = viewCount,
    slug = slug,
    artist_id = artistId,
    artist_name = artistName,
    genre_id = genreId,
    audio_url = audioUrl,
    genre_name = genreName,
)

fun SongDetail.toDto(): SongDetailDto = SongDetailDto(
    id = id,
    title = title,
    duration = duration,
    cover_image_url = coverImageUrl,
    lyrics_content = lyricsContent,
    view_count = viewCount,
    slug = slug,
    artist_id = artistId,
    artist_name = artistName,
    artist_image = artistImage,
    genre_id = genreId,
    genre_name = genreName,
    album_id = albumId,
    album_title = albumTitle,
    audio_url = audioUrl,
    album_cover = albumCover,
)

fun Song.toTopDto(): SongTopDto = SongTopDto(
    id = id,
    title = title,
    artist_id = artistId,
    album_id = null,
    genre_id = genreId,
    duration_seconds = duration,
    audio_url = audioUrl,
    cover_image_url = coverImageUrl,
    view_count = viewCount.toLong(),
    slug = slug,
    created_at = null
)

// DTO -> Domain
fun SongDto.toDomain(): Song = Song(
    id = this.id,
    title = this.title,
    duration = this.duration,
    audioUrl = this.audio_url,
    coverImageUrl = this.cover_image_url,
    viewCount = this.view_count.toInt(), // nếu domain dùng Int thì đổi thành toInt()
    slug = this.slug,
    artistId = this.artist_id,
    artistName = this.artist_name,
    genreId = this.genre_id,
    genreName = this.genre_name
)

fun SongDetailDto.toDomain(): SongDetail = SongDetail(
    id = this.id,
    title = this.title,
    duration = this.duration,
    coverImageUrl = this.cover_image_url,
    lyricsContent = this.lyrics_content,
    viewCount = this.view_count.toInt(),
    slug = this.slug,
    artistId = this.artist_id,
    artistName = this.artist_name,
    artistImage = this.artist_image,
    genreId = this.genre_id,
    genreName = this.genre_name,
    albumId = this.album_id,
    albumTitle = this.album_title,
    albumCover = this.album_cover,
    audioUrl = this.audio_url
)

fun SongTopDto.toDomain(): Song = Song(
    id = this.id,
    title = this.title,
    duration = this.duration_seconds,
    audioUrl = this.audio_url,
    coverImageUrl = this.cover_image_url,
    viewCount = this.view_count.toInt(),
    slug = this.slug,
    artistId = this.artist_id,
    artistName = "", // SongTopDto không có artist_name; để trống hoặc nullable nếu domain cho phép
    genreId = this.genre_id ?: 0,
    genreName = ""
)

package com.example.musicapp.domain.model

data class Song(
    val id: Int,
    val title: String,
    val artistName: String,
    val duration: Int,
    val audioUrl: String,
    val coverImageUrl: String?,
    val viewCount: Int,
    val slug: String?,
    val artistId: Int,
    val genreId: Int,
    val genreName: String,
    val lyricsContent: String? = null,
    val albumId: Int? = null,
    val albumTitle: String? = null,
    val albumCover: String? = null
)

data class SongDetail(
    val id: Int,
    val title: String,
    val audioUrl: String,
    val duration: Int,
    val coverImageUrl: String?,
    val lyricsContent: String?,
    val viewCount: Int,
    val slug: String?,
    val artistId: Int,
    val artistName: String,
    val artistImage: String?,
    val genreId: Int,
    val genreName: String,
    val albumId: Int?,
    val albumTitle: String?,
    val albumCover: String?
)

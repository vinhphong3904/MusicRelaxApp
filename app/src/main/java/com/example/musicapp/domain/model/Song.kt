package com.example.musicapp.domain.model

data class Song(
    val id: Int,
    val title: String,
    val duration: Int,
    val src: String,
    val coverImageUrl: String?,
    val viewCount: Int,
    val slug: String?,
    val artistId: Int,
    val artistName: String,
    val genreId: Int,
    val genreName: String
)

data class SongDetail(
    val id: Int,
    val title: String,
    val src: String,
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

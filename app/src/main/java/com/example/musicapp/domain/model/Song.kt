package com.example.musicapp.domain.model

data class Song(
    val id: Int,
    val title: String,
    val duration: Int,
    val src: String,
    val coverImageUrl: String,
    val viewCount: Int,
    val slug: String,
    val artistId: Int,
    val artistName: String,
    val genreId: Int,
    val genreName: String
)

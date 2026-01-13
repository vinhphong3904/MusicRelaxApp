package com.example.musicapp.domain.model

data class Song(
    val id: Int,
    val title: String,
    val artistId: Int,
    val albumId: Int?,
    val genreId: Int?,
    val lyrics: String?,
    val duration: Int,
    val audioUrl: String,
    val image: String?,
    val viewCount: Long,
    val status: Int,
    val createdAt: String?,
    val updatedAt: String?,
    // Fields mở rộng cho UI
    val artistName: String? = null,
    val genreName: String? = null,
    val isFavorite: Boolean = false
)

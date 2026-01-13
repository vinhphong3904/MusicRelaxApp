package com.example.musicapp.domain.model

data class Album(
    val id: Int,
    val title: String,
    val releaseDate: String,
    val description: String,
    val coverImageUrl: String,
    val createdAt: String,
    val artistId: Int,
    val artistName: String
)

package com.example.musicapp.data.model

data class ArtistDto(
    val id: Int,
    val name: String,
    val image_url: String?,
    val is_verified: Boolean,
    val slug: String
)
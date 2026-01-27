package com.example.musicapp.data.model

data class AlbumDto(
    val id: Int,
    val title: String,
    val cover_image_url: String?,
    val release_date: String?
)
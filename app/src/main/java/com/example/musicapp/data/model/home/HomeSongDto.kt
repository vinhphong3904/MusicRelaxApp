package com.example.musicapp.data.model.home

import com.example.musicapp.data.model.AlbumDto
import com.example.musicapp.data.model.ArtistDto
import com.example.musicapp.data.model.GenreDto

data class HomeSongDto(
    val id: Int,
    val title: String,
    val duration_seconds: Int,
    val audio_url: String,
    val cover_image_url: String,
    val view_count: Long,
    val slug: String,
    val created_at: String,
    val artist: ArtistDto,
    val album: AlbumDto?,
    val genre: GenreDto?
)

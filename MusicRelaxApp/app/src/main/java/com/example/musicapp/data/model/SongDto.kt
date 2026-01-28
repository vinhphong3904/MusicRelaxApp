package com.example.musicapp.data.model

import com.google.gson.annotations.SerializedName

data class SongDto(
    val id: Int,
    val title: String,
    val artist_id: Int,
    @SerializedName("artist_name")
    val artist_name: String? = null,
    val album_id: Int?,
    val genre_id: Int?,
    val duration_seconds: Int,
    val audio_url: String,
    val cover_image_url: String,
    val view_count: String,
    val slug: String,
    val created_at: String?
)

package com.example.musicapp.data.model.dto

data class HistoryDto(
    val id: String,
    val played_at: String,
    val duration_played: Int,
    val song_id: Int,
    val title: String,
    val src: String,
    val duration: Int,
    val cover_image_url: String?,
    val artist_name: String,
    val artist_id: Int
)

data class HistoriesResponse(
    val success: Boolean,
    val data: List<HistoryDto>
)

data class AddHistoryRequest(
    val songId: Int,
    val duration_played: Int
)

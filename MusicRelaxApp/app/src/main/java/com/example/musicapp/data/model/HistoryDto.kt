package com.example.musicapp.data.model

data class HistoryDto(
    val id: Int,
    val played_at: String,
    val duration_played: Int,
    val song_id: Int,
    val title: String,
    val artist_name: String
)

data class HistoryResponse(
    val success: Boolean,
    val data: List<HistoryDto>
)

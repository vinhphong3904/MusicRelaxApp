package com.example.musicapp.data.model.home

data class RecommendSongsResponse(
    val success: Boolean,
    val type: String,
    val data: List<HomeSongDto>
)
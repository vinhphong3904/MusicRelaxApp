package com.example.musicapp.data.model.home

data class TopSongsResponse(
    val success: Boolean,
    val data: List<HomeSongDto>
)
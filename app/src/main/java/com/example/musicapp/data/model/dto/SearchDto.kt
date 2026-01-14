package com.example.musicapp.data.model.dto

data class SearchResponse(
    val success: Boolean,
    val data: SearchData
)

data class SearchData(
    val artists: List<ArtistDto>,
    val albums: List<AlbumDto>,
    val songs: List<SongDto>
)
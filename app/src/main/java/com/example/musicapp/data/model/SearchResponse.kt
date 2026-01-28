package com.example.musicapp.data.model

data class SearchResponse(
    val success: Boolean,
    val data: SearchData
)

data class SearchData(
    val songs: List<SongDto>,
    val artists: List<ArtistDto>?,
    val albums: List<AlbumDto>?
)

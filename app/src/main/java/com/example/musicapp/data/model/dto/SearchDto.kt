package com.example.musicapp.data.model.dto

data class SearchResponse(
    val success: Boolean,
    val data: SearchData
)

data class SearchData(
    val artists: List<SearchArtistDto>,
    val albums: List<SearchAlbumDto>,
    val songs: List<SearchSongDto>
)

data class SearchArtistDto(
    val id: Int,
    val name: String,
    val bio: String?,
    val image_url: String?,
    val is_verified: Boolean,
    val slug: String,
    val created_at: String
)

data class SearchAlbumDto(
    val id: Int,
    val title: String,
    val artistName: String?,
    val cover_image_url: String?
)

data class SearchSongDto(
    val id: Int,
    val title: String,
    val artistName: String?,
    val cover_image_url: String?
)

package com.example.musicapp.data.model.dto

data class PlaylistDto(
    val id: Int,
    val user_id: Int?,
    val name: String,
    val description: String?,
    val is_public: Boolean,
    val thumbnail_url: String?,
    val song_count: Int?,
    val created_at: String,
    val updated_at: String?,
    val deleted_at: String? = null,
    val username: String? = null,
    val full_name: String? = null,
    val songs: List<PlaylistSongDto>? = null
)

data class PlaylistSongDto(
    val id: Int,
    val title: String,
    val duration: Int,
    val src: String,
    val cover_image_url: String?,
    val artist_name: String,
    val artist_id: Int,
    val order_index: Int?,
    val added_at: String?
)

data class PlaylistsResponse(
    val success: Boolean,
    val data: List<PlaylistDto>
)

data class PlaylistDetailResponse(
    val success: Boolean,
    val data: PlaylistDto
)

data class CreatePlaylistRequest(
    val name: String,
    val description: String,
    val isPublic: Boolean
)

data class CreatePlaylistResponse(
    val success: Boolean,
    val data: PlaylistDto
)
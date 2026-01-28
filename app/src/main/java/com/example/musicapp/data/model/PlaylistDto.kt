package com.example.musicapp.data.model

data class PlaylistDto(
    val id: Int,
    val user_id: Int,
    val name: String,
    val description: String?,
    val is_public: Boolean,
    val thumbnail_url: String?,
    val song_count: Int,
    val created_at: String?,
    val updated_at: String?
)

data class PlaylistResponse(
    val success: Boolean,
    val data: List<PlaylistDto>
)

data class PlaylistDetail(
    val id: Int,
    val name: String,
    val songs: List<SongDto>
)

data class PlaylistDetailResponse(
    val success: Boolean,
    val data: PlaylistDetail
)

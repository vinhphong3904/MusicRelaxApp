package com.example.musicapp.data.model

data class PlaylistRequest(
    val name: String,
    val description: String? = null,
    val isPublic: Boolean = true
)

data class CreatePlaylistResponse(
    val success: Boolean,
    val data: PlaylistDto
)

data class DeletePlaylistResponse(
    val success: Boolean,
    val message: String
)

package com.example.musicapp.data.model

import com.google.gson.annotations.SerializedName

data class FavoriteDto(
    @SerializedName("id")
    val song_id: Int,
    val title: String,
    val artist_name: String,
    val favorited_at: String
)

data class FavoriteResponse(
    val success: Boolean,
    val data: List<FavoriteDto>
)

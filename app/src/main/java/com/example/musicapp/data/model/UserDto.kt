package com.example.musicapp.data.model

import com.google.gson.annotations.SerializedName

data class UserModel(
    val id: Int,
    val username: String,
    val email: String,
    @SerializedName("avatar_url")
    val avatarUrl: String,
    val role: String,
    @SerializedName("created_at")
    val createdAt: String
)

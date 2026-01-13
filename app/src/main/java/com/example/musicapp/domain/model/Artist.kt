package com.example.musicapp.domain.model

data class Artist(
    val id: Int,
    val name: String,
    val bio: String?,
    val imageUrl: String?,
    val isVerified: Boolean,
    val slug: String,
    val createdAt: String
)

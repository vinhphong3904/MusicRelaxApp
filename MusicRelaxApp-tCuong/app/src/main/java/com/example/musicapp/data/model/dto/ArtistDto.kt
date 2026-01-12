package com.example.musicapp.data.model.dto

import java.time.Instant

data class ArtistDto(
    val id: Int,
    val name: String,
    val bio: String? = null,
    val imageUrl: String? = null,
    val isVerified: Boolean = false,
    val slug: String? = null,
    val status: Boolean = true,
    val createdAt: Instant,
    val updatedAt: Instant? = null,
    val deletedAt: Instant? = null
)
data class ArtistFollowDto(
    val id: Int,
    val userId: Int,
    val artistId: Int,
    val followedAt: Instant,
    val unfollowedAt: Instant? = null
)
package com.example.musicapp.data.model.dto

import java.time.Instant
import java.time.LocalDate

data class ProfileDto(
    val id: Int,
    val userId: Int,
    val fullName: String,
    val dateOfBirth: LocalDate? = null,
    val gender: String? = null,
    val phone: String? = null,
    val address: String? = null,
    val bio: String? = null,
    val songsPlayedCount: Int = 0,
    val songsDownloadedCount: Int = 0,
    val artistsFollowedCount: Int = 0,
    val createdAt: Instant,
    val updatedAt: Instant? = null
)

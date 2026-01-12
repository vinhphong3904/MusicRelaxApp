package com.example.musicapp.data.model.dto

import java.time.Instant
import java.time.LocalDate

data class AlbumDto(
    val id: Int,
    val title: String,
    val artistId: Int,
    val releaseDate: LocalDate? = null,
    val description: String? = null,
    val coverImageUrl: String? = null,
    val status: Boolean = true,
    val createdAt: Instant,
    val updatedAt: Instant? = null,
    val deletedAt: Instant? = null
)
package com.example.musicapp.data.model.dto

import java.time.Instant

data class GenreDto(
    val id: Int,
    val name: String,
    val slug: String? = null,
    val description: String? = null,
    val createdAt: Instant
)
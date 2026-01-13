package com.example.musicapp.data.model.dto

data class GenreDto(
    val id: Int,
    val name: String,
    val slug: String,
    val description: String,
    val created_at: String
)

data class GenresResponse(
    val success: Boolean,
    val data: List<GenreDto>
)

data class GenreDetailResponse(
    val success: Boolean,
    val data: GenreDto
)

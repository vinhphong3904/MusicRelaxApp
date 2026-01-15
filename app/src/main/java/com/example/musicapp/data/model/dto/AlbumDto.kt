package com.example.musicapp.data.model.dto

import com.example.musicapp.domain.model.Album

data class AlbumDto(
    val id: Int,
    val title: String,
    val release_date: String,
    val description: String,
    val cover_image_url: String,
    val created_at: String,
    val artist_id: Int,
    val artist_name: String
)


fun AlbumDto.toDomain(): Album {
    return Album(
        id = id,
        title = title,
        releaseDate = release_date,
        description = description,
        coverImageUrl = cover_image_url,
        createdAt = created_at,
        artistId = artist_id,
        artistName = artist_name
    )
}


data class AlbumsResponse(
    val success: Boolean,
    val data: List<AlbumDto>,
    val pagination: PaginationDto
)

data class AlbumDetailResponse(
    val success: Boolean,
    val data: AlbumDto
)

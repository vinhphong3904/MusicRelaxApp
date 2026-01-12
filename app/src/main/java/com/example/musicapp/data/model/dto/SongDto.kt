package com.example.musicapp.data.model.dto

import com.google.gson.annotations.SerializedName
import java.time.Instant

/**
 * DTO cho Song API response
 * 
 * VD JSON:
 * {
 *   "id": "song123",
 *   "title": "Bohemian Rhapsody",
 *   "artist": "Queen",
 *   "audio_url": "https://...",
 *   "cover_url": "https://...",
 *   "duration": 354000,
 *   "genre": "Rock"
 * }
 */
data class SongDto(
    val id: Int,
    val title: String,
    val artistId: Int,
    val albumId: Int? = null,
    val genreId: Int? = null,
    val durationSeconds: Int,
    val audioUrl: String,
    val coverImageUrl: String? = null,
    val lyricsContent: String? = null,
    val viewCount: Long = 0L,
    val slug: String? = null,
    val status: Boolean = true,
    val createdAt: Instant,
    val updatedAt: Instant? = null,
    val deletedAt: Instant? = null
)
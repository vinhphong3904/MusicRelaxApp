package com.example.musicapp.data.model.dto

import com.google.gson.annotations.SerializedName

/**
 * DTO cho Playlist API response
 * 
 * VD JSON:
 * {
 *   "id": "playlist123",
 *   "name": "My Favorites",
 *   "description": "Best songs ever",
 *   "owner_id": "user123",
 *   "songs": [...],
 *   "created_at": "2024-01-10T10:00:00Z"
 * }
 */
data class PlaylistDto(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("name")
    val name: String,
    
    @SerializedName("description")
    val description: String? = null,
    
    /**
     * ID của user tạo playlist
     */
    @SerializedName("owner_id")
    val ownerId: String,
    
    /**
     * Danh sách bài hát trong playlist
     * Có thể empty list nếu playlist mới tạo
     */
    @SerializedName("songs")
    val songs: List<SongDto> = emptyList(),
    
    /**
     * Timestamp tạo playlist
     */
    @SerializedName("created_at")
    val createdAt: String,
    
    /**
     * URL ảnh cover (lấy từ bài hát đầu tiên hoặc custom)
     */
    @SerializedName("cover_url")
    val coverUrl: String? = null
)
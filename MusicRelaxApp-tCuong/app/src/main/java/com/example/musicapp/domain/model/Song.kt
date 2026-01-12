package com.example.musicapp.domain.model.dto
import android.annotation.SuppressLint
import com.google.gson.annotations.SerializedName
/**
 * Domain Model cho Song
 * Clean model KHÔNG phụ thuộc vào API hay Database structure
 * Chỉ chứa data cần thiết cho UI
 * 
 * Khác với SongDto/SongEntity:
 * - Không có @SerializedName, @Entity annotations
 * - Không có fields dư thừa (playCount, album...)
 * - Immutable (data class) - thread safe
 */
data class SongDto(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,

    // API nên trả về object Artist rút gọn hoặc tên Artist luôn
    @SerializedName("artist_id") val artistId: Int,
    @SerializedName("artist_name") val artistName: String?,

    @SerializedName("album_id") val albumId: Int?,
    @SerializedName("duration_seconds") val duration: Int,
    @SerializedName("audio_url") val audioUrl: String,
    @SerializedName("cover_image_url") val coverUrl: String?,
    @SerializedName("lyrics_content") val lyrics: String?,
    @SerializedName("view_count") val viewCount: Long,

    // Status trả về boolean cho dễ xử lý
    @SerializedName("is_favorite") val isFavorite: Boolean = false // Field này cần Backend tính toán
) {
    /**
     * Helper function: Format duration thành MM:SS
     * VD: 235000 → "3:55"
     */
    @SuppressLint("DefaultLocale")
    fun getFormattedDuration(): String {
        val minutes = (duration / 1000) / 60
        val seconds = (duration / 1000) % 60
        return String.format("%d:%02d", minutes, seconds)
    }
}
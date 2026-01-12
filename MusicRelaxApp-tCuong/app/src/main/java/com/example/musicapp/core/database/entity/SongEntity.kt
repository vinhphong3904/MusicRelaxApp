package com.example.musicapp.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room Entity cho bảng songs
 * Lưu cache bài hát từ API
 * 
 * @Entity: Đánh dấu đây là 1 bảng trong database
 * tableName: Tên bảng (mặc định = tên class)
 */
@Entity(tableName = "downloaded_songs")
data class SongEntity(
    @PrimaryKey val songId: Int,
    val title: String,
    val artistName: String,
    val localPath: String, // Ví dụ: /storage/emulated/0/Music/App/song_1.mp3
    val coverPath: String  // Ảnh bìa cũng nên tải về offline
)
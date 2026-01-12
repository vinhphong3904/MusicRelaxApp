package com.example.musicapp.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.musicapp.core.database.dao.*
import com.example.musicapp.core.database.entity.*

/**
 * Room Database configuration
 * Định nghĩa tất cả entities và DAOs
 * 
 * Version: Tăng lên mỗi khi thay đổi schema (thêm bảng, sửa cột...)
 * exportSchema: false → không export schema ra file
 */
//@Database(
//    entities = [
//        SongEntity::class,      // Bảng songs
//        PlaylistEntity::class,  // Bảng playlists
//        UserEntity::class       // Bảng users (cache user info)
//    ],
//    version = 1,
//    exportSchema = false
//)
//abstract class AppDatabase : RoomDatabase() {
//
//    /**
//     * Lấy SongDao để thao tác với bảng songs
//     * Room tự generate implementation
//     */
//    abstract fun songDao(): SongDao
//
//    /**
//     * Lấy PlaylistDao
//     */
//    abstract fun playlistDao(): PlaylistDao
//
//    /**
//     * Lấy UserDao
//     */
//    abstract fun userDao(): UserDao
//}
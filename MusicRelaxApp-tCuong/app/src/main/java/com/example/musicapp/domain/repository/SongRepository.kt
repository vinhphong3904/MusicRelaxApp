package com.example.musicapp.domain.repository

import kotlinx.coroutines.flow.Flow

/**
 * Repository Interface cho Songs
 * Domain layer chỉ define contract, không implement
 * 
 * Implementation: SongRepositoryImpl trong data layer
 * 
 * Lợi ích:
 * - Domain layer không phụ thuộc vào data layer
 * - Dễ test (mock repository)
 * - Tuân thủ Dependency Inversion Principle
 */
//interface SongRepository {
//
//    /**
//     * Lấy danh sách tất cả songs
//     *
//     * @return Flow tự động update khi có data mới
//     */
//    fun getSongs(): Flow<List<Song>>
//
//    /**
//     * Lấy chi tiết 1 bài hát
//     *
//     * @param id: Song ID
//     * @return Song hoặc null nếu không tìm thấy
//     */
//    suspend fun getSongById(id: String): Song?
//
//    /**
//     * Tìm kiếm bài hát
//     *
//     * @param query: Từ khóa tìm kiếm
//     * @return Flow danh sách kết quả
//     */
//    fun searchSongs(query: String): Flow<List<Song>>
//}
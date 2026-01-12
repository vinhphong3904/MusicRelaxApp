package com.example.musicapp.core.database.dao

import androidx.room.*
import com.example.musicapp.core.database.entity.SongEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object cho bảng songs
 * Chứa các query để CRUD songs
 */
//@Dao
//interface SongDao {
//
//    /**
//     * Lấy TẤT CẢ bài hát từ database
//     * Sắp xếp theo title A→Z
//     *
//     * @return Flow<List<SongEntity>> - Auto update khi DB thay đổi
//     *
//     * Flow: Khi insert/update/delete → UI tự động refresh
//     */
//    @Query("SELECT * FROM songs ORDER BY title ASC")
//    fun getAllSongs(): Flow<List<SongEntity>>
//
//    /**
//     * Lấy 1 bài hát theo ID
//     *
//     * @param id: Song ID
//     * @return SongEntity hoặc null nếu không tìm thấy
//     */
//    @Query("SELECT * FROM songs WHERE id = :id")
//    suspend fun getSongById(id: String): SongEntity?
//
//    /**
//     * Tìm kiếm bài hát theo tên
//     * LIKE: Tìm gần đúng (VD: "love" match "Lovely", "Love song"...)
//     *
//     * @param query: Từ khóa tìm (tự động thêm % ở 2 đầu)
//     * @return Flow auto-update
//     */
//    @Query("SELECT * FROM songs WHERE title LIKE '%' || :query || '%'")
//    fun searchSongs(query: String): Flow<List<SongEntity>>
//
//    /**
//     * Thêm 1 bài hát vào database
//     * Nếu ID đã tồn tại → REPLACE (update)
//     *
//     * @param song: SongEntity cần insert
//     */
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertSong(song: SongEntity)
//
//    /**
//     * Thêm nhiều songs cùng lúc
//     * Dùng khi cache data từ API
//     *
//     * @param songs: List songs cần insert
//     */
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertAll(songs: List<SongEntity>)
//
//    /**
//     * Update thông tin bài hát
//     * Cập nhật toàn bộ fields của entity
//     *
//     * @param song: SongEntity với data mới
//     */
//    @Update
//    suspend fun updateSong(song: SongEntity)
//
//    /**
//     * Xóa 1 bài hát
//     *
//     * @param song: Entity cần xóa
//     */
//    @Delete
//    suspend fun deleteSong(song: SongEntity)
//
//    /**
//     * Xóa TẤT CẢ songs
//     * Dùng khi logout hoặc clear cache
//     */
//    @Query("DELETE FROM songs")
//    suspend fun deleteAll()
//}
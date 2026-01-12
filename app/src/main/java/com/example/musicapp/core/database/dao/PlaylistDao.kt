//package com.example.musicapp.core.database.dao
//
//import androidx.room.*
//import com.example.musicapp.core.database.entity.PlaylistEntity
//import kotlinx.coroutines.flow.Flow
//
///**
// * Data Access Object cho bảng playlists
// * Quản lý playlist của user
// */
//@Dao
//interface PlaylistDao {
//
//    /**
//     * Lấy tất cả playlist của 1 user
//     *
//     * @param userId: ID của user
//     * @return Flow auto-update
//     */
//    @Query("SELECT * FROM playlists WHERE user_id = :userId")
//    fun getPlaylistsByUser(userId: String): Flow<List<PlaylistEntity>>
//
//    /**
//     * Lấy playlist theo ID
//     *
//     * @param id: Playlist ID
//     */
//    @Query("SELECT * FROM playlists WHERE id = :id")
//    suspend fun getPlaylistById(id: String): PlaylistEntity?
//
//    /**
//     * Thêm / cập nhật playlist
//     *
//     * @param playlist: PlaylistEntity
//     */
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertPlaylist(playlist: PlaylistEntity)
//
//    /**
//     * Thêm nhiều playlist cùng lúc
//     * (Cache từ API nếu có)
//     */
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertAll(playlists: List<PlaylistEntity>)
//
//    /**
//     * Update playlist
//     *
//     * @param playlist: PlaylistEntity mới
//     */
//    @Update
//    suspend fun updatePlaylist(playlist: PlaylistEntity)
//
//    /**
//     * Xóa 1 playlist
//     *
//     * @param playlist: PlaylistEntity cần xóa
//     */
//    @Delete
//    suspend fun deletePlaylist(playlist: PlaylistEntity)
//
//    /**
//     * Xóa tất cả playlist của user
//     */
//    @Query("DELETE FROM playlists WHERE user_id = :userId")
//    suspend fun deletePlaylistsByUser(userId: String)
//
//    /**
//     * Xóa toàn bộ playlists
//     * Dùng khi clear cache
//     */
//    @Query("DELETE FROM playlists")
//    suspend fun deleteAll()
//}

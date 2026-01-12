package com.example.musicapp.core.network

import com.example.musicapp.data.model.dto.*
import retrofit2.http.*
import com.example.musicapp.data.model.dto.LoginRequest
import com.example.musicapp.data.model.dto.LoginResponse

/**
 * ApiService - Tổng hợp TẤT CẢ API cần cho app nghe nhạc
 * Đủ dùng để code logic trong thời gian ngắn (1 tuần)
 */
interface ApiService {

    // ================= AUTH =================

    /**
     * Đăng ký
     */
//    @POST("api/auth/register")
//    suspend fun register(
//        @Body request: RegisterRequest
//    ): UserDto

    /**
     * Đăng nhập
     */
    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): LoginResponse

    /**
     * Lấy thông tin user hiện tại
     */
//    @GET("api/auth/me")
//    suspend fun getCurrentUser(): UserDto

    /**
     * Logout (xóa token phía server nếu có)
     */
//    @POST("api/auth/logout")
//    suspend fun logout()


    // ================= SONG =================

    /**
     * Lấy danh sách bài hát (pagination)
     */
//    @GET("api/songs")
//    suspend fun getSongs(
//        @Query("page") page: Int = 1,
//        @Query("limit") limit: Int = 20
//    ): List<SongDto>

    /**
     * Lấy chi tiết 1 bài hát
     */
//    @GET("api/songs/{id}")
//    suspend fun getSongById(
//        @Path("id") id: String
//    ): SongDto

    /**
     * Tìm kiếm bài hát
     */
//    @GET("api/songs/search")
//    suspend fun searchSongs(
//        @Query("query") query: String
//    ): List<SongDto>

    /**
     * Like / Favorite bài hát
     */
//    @POST("api/songs/{id}/like")
//    suspend fun likeSong(
//        @Path("id") songId: String
//    )

    /**
     * Bỏ like bài hát
     */
//    @DELETE("api/songs/{id}/like")
//    suspend fun unlikeSong(
//        @Path("id") songId: String
//    )

    /**
     * Danh sách bài hát yêu thích
     */
//    @GET("api/songs/favorites")
//    suspend fun getFavoriteSongs(): List<SongDto>


    // ================= PLAYLIST =================

    /**
     * Lấy playlist của user
     */
//    @GET("api/playlists")
//    suspend fun getPlaylists(): List<PlaylistDto>

    /**
     * Lấy chi tiết playlist (kèm danh sách bài hát)
     */
//    @GET("api/playlists/{playlistId}")
//    suspend fun getPlaylistDetail(
//        @Path("playlistId") playlistId: String
//    ): PlaylistDto

    /**
     * Tạo playlist
     */
//    @POST("api/playlists")
//    suspend fun createPlaylist(
//        @Body request: CreatePlaylistRequest
//    ): PlaylistDto

    /**
     * Xóa playlist
     */
    @DELETE("api/playlists/{playlistId}")
    suspend fun deletePlaylist(
        @Path("playlistId") playlistId: String
    )

    /**
     * Thêm bài hát vào playlist
     */
//    @POST("api/playlists/{playlistId}/songs")
//    suspend fun addSongToPlaylist(
//        @Path("playlistId") playlistId: String,
//        @Body request: AddSongRequest
//    ): PlaylistDto

    /**
     * Xóa bài hát khỏi playlist
     */
//    @DELETE("api/playlists/{playlistId}/songs/{songId}")
//    suspend fun removeSongFromPlaylist(
//        @Path("playlistId") playlistId: String,
//        @Path("songId") songId: String
//    )


    // ================= HISTORY =================

    /**
     * Lưu lịch sử nghe nhạc
     */
//    @POST("api/history")
//    suspend fun addListeningHistory(
//        @Body request: AddHistoryRequest
//    )

    /**
     * Lấy lịch sử nghe nhạc
     */
//    @GET("api/history")
//    suspend fun getListeningHistory(): List<SongDto>
}

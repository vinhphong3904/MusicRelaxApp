package com.example.musicapp.data.api

import com.example.musicapp.data.model.home.RecommendSongsResponse
import com.example.musicapp.data.model.home.TopSongsResponse
import com.example.musicapp.data.model.login.LoginRequest
import com.example.musicapp.data.model.login.LoginResponse
import com.example.musicapp.data.model.register.RegisterRequest
import com.example.musicapp.data.model.register.RegisterResponse
import com.example.musicapp.data.model.home.MeResponse
import com.example.musicapp.data.model.PlaylistResponse
import com.example.musicapp.data.model.PlaylistDetailResponse
import com.example.musicapp.data.model.FavoriteResponse
import com.example.musicapp.data.model.HistoryResponse
import com.example.musicapp.data.model.PlaylistRequest
import com.example.musicapp.data.model.CreatePlaylistResponse
import com.example.musicapp.data.model.DeletePlaylistResponse
import com.example.musicapp.data.model.SearchResponse
import retrofit2.http.*

interface MusicApi {

    @POST("register")
    suspend fun register(
        @Body request: RegisterRequest
    ): RegisterResponse

    @POST("login")
    suspend fun login(
        @Body request: LoginRequest
    ): LoginResponse

    @GET("songs/top")
    suspend fun getTopSongs(): TopSongsResponse

    @GET("songs/recommend")
    suspend fun getRecommendSongs(): RecommendSongsResponse

    @GET("me")
    suspend fun getMe(): MeResponse

    @GET("playlists")
    suspend fun getPlaylists(): PlaylistResponse

    @POST("playlists")
    suspend fun createPlaylist(
        @Body request: PlaylistRequest
    ): CreatePlaylistResponse

    @DELETE("playlists/{id}")
    suspend fun deletePlaylist(
        @Path("id") id: Int
    ): DeletePlaylistResponse

    @GET("playlists/{id}")
    suspend fun getPlaylistDetail(
        @Path("id") id: Int
    ): PlaylistDetailResponse

    @POST("playlists/{id}/songs")
    suspend fun addSongToPlaylist(
        @Path("id") playlistId: Int,
        @Body body: Map<String, Int>
    ): Any

    // THÊM HÀM XÓA BÀI HÁT KHỎI PLAYLIST
    @DELETE("playlists/{id}/songs/{songId}")
    suspend fun removeSongFromPlaylist(
        @Path("id") playlistId: Int,
        @Path("songId") songId: Int
    ): Any

    @GET("favorites")
    suspend fun getFavorites(): FavoriteResponse

    @POST("favorites")
    suspend fun addFavorite(
        @Body body: Map<String, Int>
    ): Any

    @DELETE("favorites/{songId}")
    suspend fun removeFavorite(
        @Path("songId") songId: Int
    ): Any

    @GET("histories")
    suspend fun getHistories(): HistoryResponse

    @GET("search")
    suspend fun search(
        @Query("keyword") keyword: String
    ): SearchResponse
}

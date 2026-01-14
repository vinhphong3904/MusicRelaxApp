package com.example.musicapp.core.network

import com.example.musicapp.data.model.dto.*
import retrofit2.http.*
import okhttp3.MultipartBody

interface ApiService {

    // ========== AUTH ==========
    @POST("api/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): RegisterResponse

    @POST("api/login")
    suspend fun login(
        @Body request: LoginRequest
    ): LoginResponse

    @GET("api/me")
    suspend fun getCurrentUser(
        @Header("Authorization") token: String
    ): MeResponse

    // ========== PROFILE ==========
    @GET("api/profile")
    suspend fun getProfile(
        @Header("Authorization") token: String
    ): ProfileResponse

    @PUT("api/profile")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Body request: UpdateProfileRequest
    ): UpdateProfileResponse

    @Multipart
    @PUT("api/profile/avatar")
    suspend fun uploadAvatar(
        @Header("Authorization") token: String,
        @Part avatar: MultipartBody.Part
    ): AvatarResponse

    @DELETE("api/profile/avatar")
    suspend fun resetAvatar(
        @Header("Authorization") token: String
    ): AvatarResponse

    // ========== SONGS ==========
    @GET("api/songs")
    suspend fun getSongs(
        @Query("keyword") keyword: String? = null,
        @Query("genreId") genreId: Int? = null,
        @Query("artistId") artistId: Int? = null,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): SongsResponse

    @GET("api/songs/{id}")
    suspend fun getSongDetail(
        @Path("id") id: Int
    ): SongDetailResponse

    // ========== ALBUMS ==========
    @GET("api/albums")
    suspend fun getAlbums(
        @Header("Authorization") token: String,
        @Query("keyword") keyword: String? = null,
        @Query("artistId") artistId: Int? = null,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): AlbumsResponse

    @GET("api/albums/{id}")
    suspend fun getAlbumById(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): AlbumDetailResponse

    // ========== SEARCH ==========
    @GET("api/search")
    suspend fun search(
        @Header("Authorization") token: String,
        @Query("keyword") keyword: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 10
    ): SearchResponse

    // ========== ARTISTS ==========
    @GET("api/artists")
    suspend fun getArtists(
        @Header("Authorization") token: String,
        @Query("keyword") keyword: String? = null
    ): ArtistsResponse

    @GET("api/artists/{id}")
    suspend fun getArtistById(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): ArtistDetailResponse

    // ========== GENRES ==========
    @GET("api/genres")
    suspend fun getGenres(
        @Header("Authorization") token: String
    ): GenresResponse

    @GET("api/genres/{id}")
    suspend fun getGenreById(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): GenreDetailResponse

    // ========== PLAYLISTS ==========
    @GET("api/playlists")
    suspend fun getPlaylists(
        @Header("Authorization") token: String
    ): PlaylistsResponse

    @POST("api/playlists")
    suspend fun createPlaylist(
        @Header("Authorization") token: String,
        @Body request: CreatePlaylistRequest
    ): CreatePlaylistResponse

    @GET("api/playlists/{id}")
    suspend fun getPlaylistDetail(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): PlaylistDetailResponse

    @DELETE("api/playlists/{id}")
    suspend fun deletePlaylist(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): SimpleResponse

    @POST("api/playlists/{id}/songs")
    suspend fun addSongToPlaylist(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body body: Map<String, Int> // {"songId": 3}
    ): SimpleResponse

    @DELETE("api/playlists/{id}/songs/{songId}")
    suspend fun removeSongFromPlaylist(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Path("songId") songId: Int
    ): SimpleResponse

    // ========== FAVORITES ==========
    @GET("api/favorites")
    suspend fun getFavorites(
        @Header("Authorization") token: String
    ): FavoritesResponse

    @POST("api/favorites")
    suspend fun addFavorite(
        @Header("Authorization") token: String,
        @Body request: AddFavoriteRequest
    ): SimpleResponse

    @DELETE("api/favorites/{songId}")
    suspend fun removeFavorite(
        @Header("Authorization") token: String,
        @Path("songId") songId: Int
    ): SimpleResponse

    // ========== HISTORIES ==========
    @GET("api/histories")
    suspend fun getHistories(
        @Header("Authorization") token: String,
        @Query("limit") limit: Int = 50
    ): HistoriesResponse

    @POST("api/histories")
    suspend fun addHistory(
        @Header("Authorization") token: String,
        @Body request: AddHistoryRequest
    ): SimpleResponse

    // ========== SETTINGS ==========
    @GET("api/settings")
    suspend fun getSettings(
        @Header("Authorization") token: String
    ): SettingsResponse

    @PUT("api/settings")
    suspend fun updateSettings(
        @Header("Authorization") token: String,
        @Body request: UpdateSettingsRequest
    ): UpdateSettingsResponse
}

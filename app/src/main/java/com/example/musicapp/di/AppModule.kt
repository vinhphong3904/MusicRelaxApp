package com.example.musicapp.di

import com.example.musicapp.core.network.ApiClient
import com.example.musicapp.core.network.ApiService
import com.example.musicapp.data.remote.*
import com.example.musicapp.data.repository.*
import com.example.musicapp.domain.usecase.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // ================= CORE =================
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit = ApiClient.retrofit

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService =
        retrofit.create(ApiService::class.java)

    // ================= AUTH =================
    @Provides
    @Singleton
    fun provideAuthRemoteDataSource(api: ApiService): AuthRemoteDataSource =
        AuthRemoteDataSource(api)

    @Provides
    @Singleton
    fun provideAuthRepository(remote: AuthRemoteDataSource): AuthRepositoryInterface =
        AuthRepository(remote)

    @Provides
    @Singleton
    fun provideRegisterUseCase(repository: AuthRepositoryInterface): RegisterUseCase =
        RegisterUseCase(repository)

    @Provides
    @Singleton
    fun provideLoginUseCase(repository: AuthRepositoryInterface): LoginUseCase =
        LoginUseCase(repository)

    @Provides
    @Singleton
    fun provideGetCurrentUserUseCase(repository: AuthRepositoryInterface): GetCurrentUserUseCase =
        GetCurrentUserUseCase(repository)

    // ================= SONGS =================
    @Provides
    @Singleton
    fun provideSongRemoteDataSource(api: ApiService): SongRemoteDataSource =
        SongRemoteDataSource(api)

    @Provides
    @Singleton
    fun provideSongRepository(remote: SongRemoteDataSource): SongRepositoryInterface =
        SongRepository(remote)

    @Provides
    @Singleton
    fun provideGetSongsUseCase(repository: SongRepositoryInterface): GetSongsUseCase =
        GetSongsUseCase(repository)

    @Provides
    @Singleton
    fun provideGetSongDetailUseCase(repository: SongRepositoryInterface): GetSongDetailUseCase =
        GetSongDetailUseCase(repository)

    // ================= PROFILE =================
    @Provides
    @Singleton
    fun provideProfileRemoteDataSource(api: ApiService): ProfileRemoteDataSource =
        ProfileRemoteDataSource(api)

    @Provides
    @Singleton
    fun provideProfileRepository(remote: ProfileRemoteDataSource): ProfileRepositoryInterface =
        ProfileRepository(remote)

    @Provides
    @Singleton
    fun provideGetProfileUseCase(repository: ProfileRepositoryInterface): GetProfileUseCase =
        GetProfileUseCase(repository)

    @Provides
    @Singleton
    fun provideUpdateProfileUseCase(repository: ProfileRepositoryInterface): UpdateProfileUseCase =
        UpdateProfileUseCase(repository)

    @Provides
    @Singleton
    fun provideUploadAvatarUseCase(repository: ProfileRepositoryInterface): UploadAvatarUseCase =
        UploadAvatarUseCase(repository)

    @Provides
    @Singleton
    fun provideResetAvatarUseCase(repository: ProfileRepositoryInterface): ResetAvatarUseCase =
        ResetAvatarUseCase(repository)

    // ================= ALBUMS =================
    @Provides
    @Singleton
    fun provideAlbumRemoteDataSource(api: ApiService): AlbumRemoteDataSource =
        AlbumRemoteDataSource(api)

    @Provides
    @Singleton
    fun provideAlbumRepository(remote: AlbumRemoteDataSource): AlbumRepositoryInterface =
        AlbumRepository(remote)

    @Provides
    @Singleton
    fun provideGetAlbumsUseCase(repository: AlbumRepositoryInterface): GetAlbumsUseCase =
        GetAlbumsUseCase(repository)

    @Provides
    @Singleton
    fun provideGetAlbumDetailUseCase(repository: AlbumRepositoryInterface): GetAlbumDetailUseCase =
        GetAlbumDetailUseCase(repository)

    // ================= ARTISTS =================
    @Provides
    @Singleton
    fun provideArtistRemoteDataSource(api: ApiService): ArtistRemoteDataSource =
        ArtistRemoteDataSource(api)

    @Provides
    @Singleton
    fun provideArtistRepository(remote: ArtistRemoteDataSource): ArtistRepositoryInterface =
        ArtistRepository(remote)

    @Provides
    @Singleton
    fun provideGetArtistsUseCase(repository: ArtistRepositoryInterface): GetArtistsUseCase =
        GetArtistsUseCase(repository)

    @Provides
    @Singleton
    fun provideGetArtistDetailUseCase(repository: ArtistRepositoryInterface): GetArtistDetailUseCase =
        GetArtistDetailUseCase(repository)

    // ================= GENRES =================
    @Provides
    @Singleton
    fun provideGenreRemoteDataSource(api: ApiService): GenreRemoteDataSource =
        GenreRemoteDataSource(api)

    @Provides
    @Singleton
    fun provideGenreRepository(remote: GenreRemoteDataSource): GenreRepositoryInterface =
        GenreRepository(remote)

    @Provides
    @Singleton
    fun provideGetGenresUseCase(repo: GenreRepositoryInterface) = GetGenresUseCase(repo)

    @Provides
    @Singleton
    fun provideGetGenreDetailUseCase(repo: GenreRepositoryInterface) = GetGenreDetailUseCase(repo)

    // ================= PLAYLISTS =================
    @Provides
    @Singleton
    fun providePlaylistRemoteDataSource(api: ApiService): PlaylistRemoteDataSource =
        PlaylistRemoteDataSource(api)

    @Provides
    @Singleton
    fun providePlaylistRepository(remote: PlaylistRemoteDataSource): PlaylistRepositoryInterface =
        PlaylistRepository(remote)

    @Provides
    @Singleton
    fun provideGetPlaylistsUseCase(repo: PlaylistRepositoryInterface) = GetPlaylistsUseCase(repo)

    @Provides
    @Singleton
    fun provideCreatePlaylistUseCase(repo: PlaylistRepositoryInterface) = CreatePlaylistUse

    // ================= FAVORITES =================
    @Provides
    @Singleton
    fun provideFavoriteRemoteDataSource(api: ApiService): FavoriteRemoteDataSource =
        FavoriteRemoteDataSource(api)

    @Provides
    @Singleton
    fun provideFavoriteRepository(remote: FavoriteRemoteDataSource): FavoriteRepositoryInterface =
        FavoriteRepository(remote)

    @Provides
    @Singleton
    fun provideGetFavoritesUseCase(repo: FavoriteRepositoryInterface) = GetFavoritesUseCase(repo)

    @Provides
    @Singleton
    fun provideAddFavoriteUseCase(repo: FavoriteRepositoryInterface) = AddFavoriteUseCase(repo)

    @Provides
    @Singleton
    fun provideRemoveFavoriteUseCase(repo: FavoriteRepositoryInterface) = RemoveFavoriteUseCase(repo)
}

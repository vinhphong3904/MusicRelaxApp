package com.example.musicapp.di

import com.example.musicapp.core.network.ApiService
import com.example.musicapp.data.remote.*
import com.example.musicapp.data.repository.*
import com.example.musicapp.domain.usecase.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // ================= AUTH =================
    @Provides @Singleton
    fun provideAuthRemoteDataSource(api: ApiService): AuthRemoteDataSource =
        AuthRemoteDataSource(api)

    @Provides @Singleton
    fun provideAuthRepository(remote: AuthRemoteDataSource): AuthRepositoryInterface =
        AuthRepository(remote)

    @Provides @Singleton
    fun provideRegisterUseCase(repository: AuthRepositoryInterface) =
        RegisterUseCase(repository)

    @Provides @Singleton
    fun provideLoginUseCase(repository: AuthRepositoryInterface) =
        LoginUseCase(repository)

    @Provides @Singleton
    fun provideGetCurrentUserUseCase(repository: AuthRepositoryInterface) =
        GetCurrentUserUseCase(repository)

    // ================= SONGS =================
    @Provides @Singleton
    fun provideSongRemoteDataSource(api: ApiService): SongRemoteDataSource =
        SongRemoteDataSource(api)

    @Provides @Singleton
    fun provideSongRepository(remote: SongRemoteDataSource): SongRepositoryInterface =
        SongRepository(remote)

    @Provides @Singleton
    fun provideGetSongsUseCase(repository: SongRepositoryInterface) =
        GetSongsUseCase(repository)

    @Provides @Singleton
    fun provideGetSongDetailUseCase(repository: SongRepositoryInterface) =
        GetSongDetailUseCase(repository)

    // ================= PROFILE =================
    @Provides @Singleton
    fun provideProfileRemoteDataSource(api: ApiService): ProfileRemoteDataSource =
        ProfileRemoteDataSource(api)

    @Provides @Singleton
    fun provideProfileRepository(remote: ProfileRemoteDataSource): ProfileRepositoryInterface =
        ProfileRepository(remote)

    @Provides @Singleton
    fun provideGetProfileUseCase(repository: ProfileRepositoryInterface) =
        GetProfileUseCase(repository)

    @Provides @Singleton
    fun provideUpdateProfileUseCase(repository: ProfileRepositoryInterface) =
        UpdateProfileUseCase(repository)

    @Provides @Singleton
    fun provideUploadAvatarUseCase(repository: ProfileRepositoryInterface) =
        UploadAvatarUseCase(repository)

    @Provides @Singleton
    fun provideResetAvatarUseCase(repository: ProfileRepositoryInterface) =
        ResetAvatarUseCase(repository)

    // ================= ALBUMS =================
    @Provides @Singleton
    fun provideAlbumRemoteDataSource(api: ApiService): AlbumRemoteDataSource =
        AlbumRemoteDataSource(api)

    @Provides @Singleton
    fun provideAlbumRepository(remote: AlbumRemoteDataSource): AlbumRepositoryInterface =
        AlbumRepository(remote)

    @Provides @Singleton
    fun provideGetAlbumsUseCase(repository: AlbumRepositoryInterface) =
        GetAlbumsUseCase(repository)

    @Provides @Singleton
    fun provideGetAlbumDetailUseCase(repository: AlbumRepositoryInterface) =
        GetAlbumDetailUseCase(repository)

    // ================= ARTISTS =================
    @Provides @Singleton
    fun provideArtistRemoteDataSource(api: ApiService): ArtistRemoteDataSource =
        ArtistRemoteDataSource(api)

    @Provides @Singleton
    fun provideArtistRepository(remote: ArtistRemoteDataSource): ArtistRepositoryInterface =
        ArtistRepository(remote)

    @Provides @Singleton
    fun provideGetArtistsUseCase(repository: ArtistRepositoryInterface) =
        GetArtistsUseCase(repository)

    @Provides @Singleton
    fun provideGetArtistDetailUseCase(repository: ArtistRepositoryInterface) =
        GetArtistDetailUseCase(repository)

    // ================= GENRES =================
    @Provides @Singleton
    fun provideGenreRemoteDataSource(api: ApiService): GenreRemoteDataSource =
        GenreRemoteDataSource(api)

    @Provides @Singleton
    fun provideGenreRepository(remote: GenreRemoteDataSource): GenreRepositoryInterface =
        GenreRepository(remote)

    @Provides @Singleton
    fun provideGetGenresUseCase(repo: GenreRepositoryInterface) =
        GetGenresUseCase(repo)

    @Provides @Singleton
    fun provideGetGenreDetailUseCase(repo: GenreRepositoryInterface) =
        GetGenreDetailUseCase(repo)

    // ================= PLAYLISTS =================
    @Provides @Singleton
    fun providePlaylistRemoteDataSource(api: ApiService): PlaylistRemoteDataSource =
        PlaylistRemoteDataSource(api)

    @Provides @Singleton
    fun providePlaylistRepository(remote: PlaylistRemoteDataSource): PlaylistRepositoryInterface =
        PlaylistRepository(remote)

    @Provides @Singleton
    fun provideGetPlaylistsUseCase(repo: PlaylistRepositoryInterface) =
        GetPlaylistsUseCase(repo)

    @Provides @Singleton
    fun provideCreatePlaylistUseCase(repo: PlaylistRepositoryInterface) =
        CreatePlaylistUseCase(repo)

    @Provides @Singleton
    fun provideGetPlaylistDetailUseCase(repo: PlaylistRepositoryInterface) =
        GetPlaylistDetailUseCase(repo)

    @Provides @Singleton
    fun provideDeletePlaylistUseCase(repo: PlaylistRepositoryInterface) =
        DeletePlaylistUseCase(repo)

    @Provides @Singleton
    fun provideAddSongToPlaylistUseCase(repo: PlaylistRepositoryInterface) =
        AddSongToPlaylistUseCase(repo)

    @Provides @Singleton
    fun provideRemoveSongFromPlaylistUseCase(repo: PlaylistRepositoryInterface) =
        RemoveSongFromPlaylistUseCase(repo)

    // ================= FAVORITES =================
    @Provides @Singleton
    fun provideFavoriteRemoteDataSource(api: ApiService): FavoriteRemoteDataSource =
        FavoriteRemoteDataSource(api)

    @Provides @Singleton
    fun provideFavoriteRepository(remote: FavoriteRemoteDataSource): FavoriteRepositoryInterface =
        FavoriteRepository(remote)

    @Provides @Singleton
    fun provideGetFavoritesUseCase(repo: FavoriteRepositoryInterface) =
        GetFavoritesUseCase(repo)

    @Provides @Singleton
    fun provideAddFavoriteUseCase(repo: FavoriteRepositoryInterface) =
        AddFavoriteUseCase(repo)

    @Provides @Singleton
    fun provideRemoveFavoriteUseCase(repo: FavoriteRepositoryInterface) =
        RemoveFavoriteUseCase(repo)

    // ================= HISTORIES =================
    @Provides @Singleton
    fun provideHistoryRemoteDataSource(api: ApiService): HistoryRemoteDataSource =
        HistoryRemoteDataSource(api)

    @Provides @Singleton
    fun provideHistoryRepository(remote: HistoryRemoteDataSource): HistoryRepositoryInterface =
        HistoryRepository(remote)

    @Provides @Singleton
    fun provideGetHistoriesUseCase(repo: HistoryRepositoryInterface) =
        GetHistoriesUseCase(repo)

    @Provides @Singleton
    fun provideAddHistoryUseCase(repo: HistoryRepositoryInterface) =
        AddHistoryUseCase(repo)

    // ================= SEARCH =================
    @Provides @Singleton
    fun provideSearchRemoteDataSource(api: ApiService): SearchRemoteDataSource =
        SearchRemoteDataSource(api)

    @Provides @Singleton
    fun provideSearchRepository(remote: SearchRemoteDataSource): SearchRepositoryInterface =
        SearchRepository(remote)

    @Provides @Singleton
    fun provideSearchUseCase(repo: SearchRepositoryInterface) =
        SearchUseCase(repo)


    // ================= SETTINGS =================
    @Provides
    @Singleton
    fun provideSettingsRemoteDataSource(api: ApiService): SettingsRemoteDataSource =
        SettingsRemoteDataSource(api)

    @Provides
    @Singleton
    fun provideSettingsRepository(remote: SettingsRemoteDataSource): SettingsRepositoryInterface =
        SettingsRepository(remote)

    @Provides
    @Singleton
    fun provideGetSettingsUseCase(repo: SettingsRepositoryInterface) =
        GetSettingsUseCase(repo)

    @Provides
    @Singleton
    fun provideUpdateSettingsUseCase(repo: SettingsRepositoryInterface) =
        UpdateSettingsUseCase(repo)
}

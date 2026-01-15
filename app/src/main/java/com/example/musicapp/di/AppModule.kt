package com.example.musicapp.di

import com.example.musicapp.core.datastore.UserDataStore
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
    @Provides
    @Singleton
    fun provideAuthRemoteDataSource(
        api: ApiService
    ): AuthRemoteDataSource =
        AuthRemoteDataSource(api)

    @Provides
    @Singleton
    fun provideRegisterUseCase(
        repository: AuthRepositoryInterface
    ) = RegisterUseCase(repository)

    @Provides
    @Singleton
    fun provideLoginUseCase(
        repository: AuthRepositoryInterface
    ) = LoginUseCase(repository)

    @Provides
    @Singleton
    fun provideGetCurrentUserUseCase(
        repository: AuthRepositoryInterface
    ) = GetCurrentUserUseCase(repository)

    // ================= SONGS =================
    @Provides
    @Singleton
    fun provideSongRemoteDataSource(
        api: ApiService
    ): SongRemoteDataSource =
        SongRemoteDataSource(api)

    @Provides
    @Singleton
    fun provideGetSongsUseCase(
        repository: SongRepositoryInterface
    ) = GetSongsUseCase(repository)

    @Provides
    @Singleton
    fun provideGetSongDetailUseCase(
        repository: SongRepositoryInterface
    ) = GetSongDetailUseCase(repository)

    @Provides
    @Singleton
    fun provideGetTopSongsUseCase(
        repository: SongRepositoryInterface
    ) = GetTopSongsUseCase(repository)

    @Provides
    @Singleton
    fun provideGetRecommendSongsUseCase(
        repository: SongRepositoryInterface
    ) = GetRecommendSongsUseCase(repository)

    // ================= PROFILE =================
    @Provides
    @Singleton
    fun provideProfileRemoteDataSource(
        api: ApiService
    ): ProfileRemoteDataSource =
        ProfileRemoteDataSource(api)

    @Provides
    @Singleton
    fun provideGetProfileUseCase(
        repository: ProfileRepositoryInterface
    ) = GetProfileUseCase(repository)

    @Provides
    @Singleton
    fun provideUpdateProfileUseCase(
        repository: ProfileRepositoryInterface
    ) = UpdateProfileUseCase(repository)

    @Provides
    @Singleton
    fun provideUploadAvatarUseCase(
        repository: ProfileRepositoryInterface
    ) = UploadAvatarUseCase(repository)

    @Provides
    @Singleton
    fun provideResetAvatarUseCase(
        repository: ProfileRepositoryInterface
    ) = ResetAvatarUseCase(repository)

    // ================= ALBUMS =================
    @Provides
    @Singleton
    fun provideAlbumRemoteDataSource(
        api: ApiService
    ): AlbumRemoteDataSource =
        AlbumRemoteDataSource(api)

    @Provides
    @Singleton
    fun provideGetAlbumsUseCase(
        repository: AlbumRepositoryInterface
    ) = GetAlbumsUseCase(repository)

    @Provides
    @Singleton
    fun provideGetAlbumDetailUseCase(
        repository: AlbumRepositoryInterface
    ) = GetAlbumDetailUseCase(repository)

    // ================= ARTISTS =================
    @Provides
    @Singleton
    fun provideArtistRemoteDataSource(
        api: ApiService
    ): ArtistRemoteDataSource =
        ArtistRemoteDataSource(api)

    @Provides
    @Singleton
    fun provideGetArtistsUseCase(
        repository: ArtistRepositoryInterface
    ) = GetArtistsUseCase(repository)

    @Provides
    @Singleton
    fun provideGetArtistDetailUseCase(
        repository: ArtistRepositoryInterface
    ) = GetArtistDetailUseCase(repository)

    // ================= GENRES =================
    @Provides
    @Singleton
    fun provideGenreRemoteDataSource(
        api: ApiService
    ): GenreRemoteDataSource =
        GenreRemoteDataSource(api)

    @Provides
    @Singleton
    fun provideGetGenresUseCase(
        repository: GenreRepositoryInterface
    ) = GetGenresUseCase(repository)

    @Provides
    @Singleton
    fun provideGetGenreDetailUseCase(
        repository: GenreRepositoryInterface
    ) = GetGenreDetailUseCase(repository)

    // ================= PLAYLISTS =================
    @Provides
    @Singleton
    fun providePlaylistRemoteDataSource(
        api: ApiService
    ): PlaylistRemoteDataSource =
        PlaylistRemoteDataSource(api)

    @Provides
    @Singleton
    fun provideGetPlaylistsUseCase(
        repository: PlaylistRepositoryInterface
    ) = GetPlaylistsUseCase(repository)

    @Provides
    @Singleton
    fun provideCreatePlaylistUseCase(
        repository: PlaylistRepositoryInterface
    ) = CreatePlaylistUseCase(repository)

    @Provides
    @Singleton
    fun provideGetPlaylistDetailUseCase(
        repository: PlaylistRepositoryInterface
    ) = GetPlaylistDetailUseCase(repository)

    @Provides
    @Singleton
    fun provideDeletePlaylistUseCase(
        repository: PlaylistRepositoryInterface
    ) = DeletePlaylistUseCase(repository)

    @Provides
    @Singleton
    fun provideAddSongToPlaylistUseCase(
        repository: PlaylistRepositoryInterface
    ) = AddSongToPlaylistUseCase(repository)

    @Provides
    @Singleton
    fun provideRemoveSongFromPlaylistUseCase(
        repository: PlaylistRepositoryInterface
    ) = RemoveSongFromPlaylistUseCase(repository)

    // ================= FAVORITES =================
    @Provides
    @Singleton
    fun provideFavoriteRemoteDataSource(
        api: ApiService
    ): FavoriteRemoteDataSource =
        FavoriteRemoteDataSource(api)

    @Provides
    @Singleton
    fun provideGetFavoritesUseCase(
        repository: FavoriteRepositoryInterface
    ) = GetFavoritesUseCase(repository)

    @Provides
    @Singleton
    fun provideAddFavoriteUseCase(
        repository: FavoriteRepositoryInterface
    ) = AddFavoriteUseCase(repository)

    @Provides
    @Singleton
    fun provideRemoveFavoriteUseCase(
        repository: FavoriteRepositoryInterface
    ) = RemoveFavoriteUseCase(repository)

    // ================= HISTORIES =================
    @Provides
    @Singleton
    fun provideHistoryRemoteDataSource(
        api: ApiService
    ): HistoryRemoteDataSource =
        HistoryRemoteDataSource(api)

    @Provides
    @Singleton
    fun provideGetHistoriesUseCase(
        repository: HistoryRepositoryInterface
    ) = GetHistoriesUseCase(repository)

    @Provides
    @Singleton
    fun provideAddHistoryUseCase(
        repository: HistoryRepositoryInterface
    ) = AddHistoryUseCase(repository)

    // ================= SEARCH =================
    @Provides
    @Singleton
    fun provideSearchRemoteDataSource(
        api: ApiService
    ): SearchRemoteDataSource =
        SearchRemoteDataSource(api)

    @Provides
    @Singleton
    fun provideSearchUseCase(
        repository: SearchRepositoryInterface
    ) = SearchUseCase(repository)

    // ================= SETTINGS =================
    @Provides
    @Singleton
    fun provideSettingsRemoteDataSource(
        api: ApiService
    ): SettingsRemoteDataSource =
        SettingsRemoteDataSource(api)

    @Provides
    @Singleton
    fun provideGetSettingsUseCase(
        repository: SettingsRepositoryInterface
    ) = GetSettingsUseCase(repository)

    @Provides
    @Singleton
    fun provideUpdateSettingsUseCase(
        repository: SettingsRepositoryInterface
    ) = UpdateSettingsUseCase(repository)
}

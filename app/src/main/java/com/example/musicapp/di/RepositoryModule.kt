package com.example.musicapp.di

import com.example.musicapp.data.repository.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds @Singleton
    abstract fun bindAuthRepository(
        impl: AuthRepository
    ): AuthRepositoryInterface

    @Binds @Singleton
    abstract fun bindSongRepository(
        impl: SongRepository
    ): SongRepositoryInterface

    @Binds @Singleton
    abstract fun bindProfileRepository(
        impl: ProfileRepository
    ): ProfileRepositoryInterface

    @Binds @Singleton
    abstract fun bindAlbumRepository(
        impl: AlbumRepository
    ): AlbumRepositoryInterface

    @Binds @Singleton
    abstract fun bindArtistRepository(
        impl: ArtistRepository
    ): ArtistRepositoryInterface

    @Binds @Singleton
    abstract fun bindGenreRepository(
        impl: GenreRepository
    ): GenreRepositoryInterface

    @Binds @Singleton
    abstract fun bindPlaylistRepository(
        impl: PlaylistRepository
    ): PlaylistRepositoryInterface

    @Binds @Singleton
    abstract fun bindFavoriteRepository(
        impl: FavoriteRepository
    ): FavoriteRepositoryInterface

    @Binds @Singleton
    abstract fun bindHistoryRepository(
        impl: HistoryRepository
    ): HistoryRepositoryInterface

    @Binds @Singleton
    abstract fun bindSearchRepository(
        impl: SearchRepository
    ): SearchRepositoryInterface

    @Binds @Singleton
    abstract fun bindSettingsRepository(
        impl: SettingsRepository
    ): SettingsRepositoryInterface
}

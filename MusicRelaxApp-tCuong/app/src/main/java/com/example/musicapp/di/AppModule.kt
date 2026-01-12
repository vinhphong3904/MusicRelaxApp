package com.example.musicapp.di

import android.content.Context
import androidx.media3.exoplayer.ExoPlayer
import com.example.musicapp.core.datastore.UserDataStore
import com.example.musicapp.core.network.ApiService
import com.example.musicapp.data.repository.AuthRepositoryImpl
import com.example.musicapp.domain.repository.AuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt Module cho app-level dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    /**
     * Provide Repository implementations
     */
    @Provides
    @Singleton
    fun provideAuthRepository(
        api: ApiService,
        store: UserDataStore
    ): AuthRepository {
        return AuthRepositoryImpl(api, store)
    }
    
//    @Provides
//    @Singleton
//    fun provideSongRepository(
//        impl: SongRepositoryImpl
//    ): SongRepository = impl
//
    /**
     * Provide ExoPlayer instance
     */
    @Provides
    @Singleton
    fun provideExoPlayer(
        @ApplicationContext context: Context
    ): ExoPlayer {
        return ExoPlayer.Builder(context).build()
    }
}

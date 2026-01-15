package com.example.musicapp.data.repository

import com.example.musicapp.data.remote.PlaylistRemoteDataSource
import com.example.musicapp.data.model.dto.*
import javax.inject.Inject

interface PlaylistRepositoryInterface {
    suspend fun getPlaylists(token: String): List<PlaylistDto>
    suspend fun createPlaylist(token: String, request: CreatePlaylistRequest): PlaylistDto
    suspend fun getPlaylistDetail(token: String, id: Int): PlaylistDto
    suspend fun deletePlaylist(token: String, id: Int): String?
    suspend fun addSong(token: String, id: Int, songId: Int): String?
    suspend fun removeSong(token: String, id: Int, songId: Int): String?
}

class PlaylistRepository @Inject constructor(private val remoteDataSource: PlaylistRemoteDataSource) : PlaylistRepositoryInterface {
    override suspend fun getPlaylists(token: String) = remoteDataSource.fetchPlaylists(token).data
    override suspend fun createPlaylist(token: String, request: CreatePlaylistRequest) = remoteDataSource.createPlaylist(token, request).data
    override suspend fun getPlaylistDetail(token: String, id: Int) = remoteDataSource.fetchPlaylistDetail(token, id).data
    override suspend fun deletePlaylist(token: String, id: Int) = remoteDataSource.deletePlaylist(token, id).message
    override suspend fun addSong(token: String, id: Int, songId: Int) = remoteDataSource.addSong(token, id, songId).message
    override suspend fun removeSong(token: String, id: Int, songId: Int) = remoteDataSource.removeSong(token, id, songId).message
}

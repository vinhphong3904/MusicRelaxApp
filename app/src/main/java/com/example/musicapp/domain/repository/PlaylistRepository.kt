package com.example.musicapp.domain.repository

import com.example.musicapp.data.remote.PlaylistRemoteDataSource
import com.example.musicapp.data.model.dto.*

interface PlaylistRepositoryInterface {
    suspend fun getPlaylists(token: String): List<PlaylistDto>
    suspend fun createPlaylist(token: String, request: CreatePlaylistRequest): PlaylistDto
    suspend fun getPlaylistDetail(token: String, id: Int): PlaylistDto
    suspend fun deletePlaylist(token: String, id: Int): String
    suspend fun addSong(token: String, id: Int, songId: Int): String
    suspend fun removeSong(token: String, id: Int, songId: Int): String
}

class PlaylistRepository(private val remote: PlaylistRemoteDataSource) : PlaylistRepositoryInterface {
    override suspend fun getPlaylists(token: String) = remote.fetchPlaylists(token).data
    override suspend fun createPlaylist(token: String, request: CreatePlaylistRequest) = remote.createPlaylist(token, request).data
    override suspend fun getPlaylistDetail(token: String, id: Int) = remote.fetchPlaylistDetail(token, id).data
    override suspend fun deletePlaylist(token: String, id: Int) = remote.deletePlaylist(token, id).message
    override suspend fun addSong(token: String, id: Int, songId: Int) = remote.addSong(token, id, songId).message
    override suspend fun removeSong(token: String, id: Int, songId: Int) = remote.removeSong(token, id, songId).message
}

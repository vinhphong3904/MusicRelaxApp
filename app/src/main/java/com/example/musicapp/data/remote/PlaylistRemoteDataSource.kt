package com.example.musicapp.data.remote

import com.example.musicapp.core.network.ApiService
import com.example.musicapp.data.model.dto.*

class PlaylistRemoteDataSource(private val api: ApiService) {
    suspend fun fetchPlaylists(token: String) = api.getPlaylists("Bearer $token")
    suspend fun createPlaylist(token: String, request: CreatePlaylistRequest) = api.createPlaylist("Bearer $token", request)
    suspend fun fetchPlaylistDetail(token: String, id: Int) = api.getPlaylistDetail("Bearer $token", id)
    suspend fun deletePlaylist(token: String, id: Int) = api.deletePlaylist("Bearer $token", id)
    suspend fun addSong(token: String, id: Int, songId: Int) = api.addSongToPlaylist("Bearer $token", id, mapOf("songId" to songId))
    suspend fun removeSong(token: String, id: Int, songId: Int) = api.removeSongFromPlaylist("Bearer $token", id, songId)
}

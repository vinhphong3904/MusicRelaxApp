package com.example.musicapp.domain.usecase

import com.example.musicapp.data.repository.PlaylistRepositoryInterface
import com.example.musicapp.domain.model.*
import com.example.musicapp.domain.model.Playlist

class GetPlaylistsUseCase(private val repo: PlaylistRepositoryInterface) {
    suspend operator fun invoke(token: String) = repo.getPlaylists(token)
}

class CreatePlaylistUseCase(private val repo: PlaylistRepositoryInterface) {
    suspend operator fun invoke(token: String, request: CreatePlaylistRequest) = repo.createPlaylist(token, request)
}

class GetPlaylistDetailUseCase(private val repo: PlaylistRepositoryInterface) {
    suspend operator fun invoke(token: String, id: Int) = repo.getPlaylistDetail(token, id)
}

class DeletePlaylistUseCase(private val repo: PlaylistRepositoryInterface) {
    suspend operator fun invoke(token: String, id: Int) = repo.deletePlaylist(token, id)
}

class AddSongToPlaylistUseCase(private val repo: PlaylistRepositoryInterface) {
    suspend operator fun invoke(token: String, id: Int, songId: Int) = repo.addSong(token, id, songId)
}

class RemoveSongFromPlaylistUseCase(private val repo: PlaylistRepositoryInterface) {
    suspend operator fun invoke(token: String, id: Int, songId: Int) = repo.removeSong(token, id, songId)
}

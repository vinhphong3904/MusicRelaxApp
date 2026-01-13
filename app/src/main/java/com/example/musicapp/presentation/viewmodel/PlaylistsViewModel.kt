package com.example.musicapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapp.domain.model.Artist
import com.example.musicapp.domain.usecase.GetPlaylistsUseCase
import com.example.musicapp.domain.usecase.CreatePlaylistUseCase
import com.example.musicapp.domain.usecase.GetPlaylistDetailUseCase
import com.example.musicapp.domain.usecase.DeletePlaylistUseCase
import com.example.musicapp.domain.usecase.AddSongToPlaylistUseCase
import com.example.musicapp.domain.usecase.RemoveSongFromPlaylistUseCase
import com.example.musicapp.data.model.dto.PlaylistDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaylistsViewModel @Inject constructor(
    private val getPlaylistsUseCase: GetPlaylistsUseCase,
    private val createPlaylistUseCase: CreatePlaylistUseCase,
    private val getPlaylistDetailUseCase: GetPlaylistDetailUseCase,
    private val deletePlaylistUseCase: DeletePlaylistUseCase,
    private val addSongToPlaylistUseCase: AddSongToPlaylistUseCase,
    private val removeSongFromPlaylistUseCase: RemoveSongFromPlaylistUseCase
) : ViewModel() {

    private val _playlists = MutableStateFlow<List<PlaylistDto>>(emptyList())
    val playlists: StateFlow<List<PlaylistDto>> = _playlists

    private val _playlistDetail = MutableStateFlow<PlaylistDto?>(null)
    val playlistDetail: StateFlow<PlaylistDto?> = _playlistDetail

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    fun loadPlaylists(token: String) {
        viewModelScope.launch {
            _playlists.value = getPlaylistsUseCase(token)
        }
    }

    fun createPlaylist(token: String, request: CreatePlaylistRequest) {
        viewModelScope.launch {
            val result = createPlaylistUseCase(token, request)
            _message.value = "Created playlist: ${result.name}"
        }
    }

    fun loadPlaylistDetail(token: String, id: Int) {
        viewModelScope.launch {
            _playlistDetail.value = getPlaylistDetailUseCase(token, id)
        }
    }

    fun deletePlaylist(token: String, id: Int) {
        viewModelScope.launch {
            _message.value = deletePlaylistUseCase(token, id)
        }
    }

    fun addSong(token: String, id: Int, songId: Int) {
        viewModelScope.launch {
            _message.value = addSongToPlaylistUseCase(token, id, songId)
        }
    }

    fun removeSong(token: String, id: Int, songId: Int) {
        viewModelScope.launch {
            _message.value = removeSongFromPlaylistUseCase(token, id, songId)
        }
    }
}

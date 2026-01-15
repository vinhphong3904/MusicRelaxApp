package com.example.musicapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapp.data.mapper.toDto
import com.example.musicapp.data.mapper.toTopDto
import com.example.musicapp.data.model.dto.*
import com.example.musicapp.data.repository.AuthRepositoryInterface
import com.example.musicapp.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SongsViewModel @Inject constructor(
    private val songUseCases: SongUseCases,
    private val authRepository: AuthRepositoryInterface
) : ViewModel() {

    // ===== TOKEN =====
    val tokenState: StateFlow<String?> =
        authRepository.tokenFlow.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    // ===== SONG LIST =====
    private val _songs = MutableStateFlow<List<SongDto>>(emptyList())
    val songs: StateFlow<List<SongDto>> = _songs

    // ===== SONG DETAIL =====
    private val _songDetail = MutableStateFlow<SongDetailDto?>(null)
    val songDetail: StateFlow<SongDetailDto?> = _songDetail

    // ===== TOP SONGS =====
    private val _topSongs = MutableStateFlow<List<SongTopDto>>(emptyList())
    val topSongs: StateFlow<List<SongTopDto>> = _topSongs

    // ===== RECOMMEND SONGS =====
    private val _recommendSongs = MutableStateFlow<List<SongTopDto>>(emptyList())
    val recommendSongs: StateFlow<List<SongTopDto>> = _recommendSongs

    // ===== MESSAGE =====
    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    // ===== LOAD SONGS =====
    fun loadSongs(page: Int = 1, limit: Int = 20) {
        viewModelScope.launch {
            songUseCases.getSongs(page, limit).collect { result ->
                result.onSuccess { list ->
                    _songs.value = list.map { it.toDto() } // map domain → DTO
                    _message.value = null
                }
                result.onFailure {
                    _message.value = it.message
                }
            }
        }
    }

    // ===== LOAD SONG DETAIL =====
    fun loadSongDetail(id: Int) {
        viewModelScope.launch {
            songUseCases.getSongDetail(id).collect { result ->
                result.onSuccess { song ->
                    _songDetail.value = song.toDto() // map domain → DTO
                    _message.value = null
                }
                result.onFailure {
                    _message.value = it.message
                }
            }
        }
    }

    // ===== LOAD TOP SONGS =====
    fun loadTopSongs() {
        viewModelScope.launch {
            songUseCases.getTopSongs().collect { result ->
                result.onSuccess { list ->
                    _topSongs.value = list.map { it.toTopDto() } // map domain → TopDto
                    _message.value = null
                }
                result.onFailure {
                    _message.value = it.message
                }
            }
        }
    }

    // ===== LOAD RECOMMEND SONGS =====
    fun loadRecommendSongs() {
        viewModelScope.launch {
            songUseCases.getRecommendSongs().collect { result ->
                result.onSuccess { list ->
                    _recommendSongs.value = list.map { it.toTopDto() }
                    _message.value = null
                }
                result.onFailure {
                    _message.value = it.message
                }
            }
        }
    }
}

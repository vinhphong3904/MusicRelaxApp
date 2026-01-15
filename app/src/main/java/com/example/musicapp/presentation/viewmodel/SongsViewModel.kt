package com.example.musicapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapp.data.model.dto.*
import com.example.musicapp.data.repository.AuthRepositoryInterface
import com.example.musicapp.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SongsViewModel @Inject constructor(
    private val getSongsUseCase: GetSongsUseCase,
    private val getSongDetailUseCase: GetSongDetailUseCase,
    private val getTopSongsUseCase: GetTopSongsUseCase,
    private val getRecommendSongsUseCase: GetRecommendSongsUseCase,
    private val authRepository: AuthRepositoryInterface
) : ViewModel() {

    // ===== TOKEN =====
    private val tokenState: StateFlow<String?> =
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
    fun loadSongs(
        keyword: String? = null,
        genreId: Int? = null,
        artistId: Int? = null,
        page: Int = 1,
        limit: Int = 20
    ) {
        viewModelScope.launch {
            runCatching {
                getSongsUseCase(keyword, genreId, artistId, page, limit)
            }.onSuccess {
                _songs.value = it
                _message.value = null
            }.onFailure {
                _message.value = it.message
            }
        }
    }

    // ===== LOAD SONG DETAIL =====
    fun loadSongDetail(id: Int) {
        viewModelScope.launch {
            runCatching {
                getSongDetailUseCase(id)
            }.onSuccess {
                _songDetail.value = it
                _message.value = null
            }.onFailure {
                _message.value = it.message
            }
        }
    }

    // ===== LOAD TOP SONGS =====
    fun loadTopSongs() {
        viewModelScope.launch {
            runCatching {
                getTopSongsUseCase()
            }.onSuccess {
                _topSongs.value = it
                _message.value = null
            }.onFailure {
                _message.value = it.message
            }
        }
    }

    // ===== LOAD RECOMMEND SONGS (AUTH REQUIRED) =====
    fun loadRecommendSongs() {
        viewModelScope.launch {
            runCatching {
                getRecommendSongsUseCase()
            }.onSuccess {
                _recommendSongs.value = it
                _message.value = null
            }.onFailure {
                _message.value = it.message
            }
        }
    }
}

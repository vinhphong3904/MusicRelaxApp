package com.example.musicapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapp.data.model.dto.SongDto
import com.example.musicapp.data.model.dto.SongDetailDto
import com.example.musicapp.data.model.dto.SongTopDto
import com.example.musicapp.domain.usecase.GetSongsUseCase
import com.example.musicapp.domain.usecase.GetSongDetailUseCase
import com.example.musicapp.domain.usecase.GetTopSongsUseCase
import com.example.musicapp.domain.usecase.GetRecommendSongsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SongsViewModel @Inject constructor(
    private val getSongsUseCase: GetSongsUseCase,
    private val getSongDetailUseCase: GetSongDetailUseCase,
    private val getTopSongsUseCase: GetTopSongsUseCase,
    private val getRecommendSongsUseCase: GetRecommendSongsUseCase
) : ViewModel() {

    // State cho danh sách bài hát (search/list)
    private val _songs = MutableStateFlow<List<SongDto>>(emptyList())
    val songs: StateFlow<List<SongDto>> = _songs

    // State cho chi tiết bài hát
    private val _songDetail = MutableStateFlow<SongDetailDto?>(null)
    val songDetail: StateFlow<SongDetailDto?> = _songDetail

    // State cho Top 10
    private val _topSongs = MutableStateFlow<List<SongTopDto>>(emptyList())
    val topSongs: StateFlow<List<SongTopDto>> = _topSongs

    // State cho Recommend
    private val _recommendSongs = MutableStateFlow<List<SongTopDto>>(emptyList())
    val recommendSongs: StateFlow<List<SongTopDto>> = _recommendSongs

    // State cho thông báo (ví dụ lỗi hoặc message khác)
    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    // Lấy danh sách bài hát (search/list)
    fun loadSongs(
        keyword: String? = null,
        genreId: Int? = null,
        artistId: Int? = null,
        page: Int = 1,
        limit: Int = 20
    ) {
        viewModelScope.launch {
            try {
                _songs.value = getSongsUseCase(keyword, genreId, artistId, page, limit)
                _message.value = null
            } catch (e: Exception) {
                _message.value = e.message
            }
        }
    }

    // Lấy chi tiết bài hát
    fun loadSongDetail(id: Int) {
        viewModelScope.launch {
            try {
                _songDetail.value = getSongDetailUseCase(id)
                _message.value = null
            } catch (e: Exception) {
                _message.value = e.message
            }
        }
    }

    // Lấy Top 10 bài hát
    fun loadTopSongs() {
        viewModelScope.launch {
            try {
                _topSongs.value = getTopSongsUseCase()
                _message.value = null
            } catch (e: Exception) {
                _message.value = e.message
            }
        }
    }

    // Lấy Recommend bài hát
    fun loadRecommendSongs(token: String) {
        viewModelScope.launch {
            try {
                _recommendSongs.value = getRecommendSongsUseCase(token)
                _message.value = null
            } catch (e: Exception) {
                _message.value = e.message
            }
        }
    }
}
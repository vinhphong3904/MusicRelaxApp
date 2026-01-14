package com.example.musicapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapp.domain.model.Song
import com.example.musicapp.domain.model.SongDetail
import com.example.musicapp.domain.usecase.GetSongsUseCase
import com.example.musicapp.domain.usecase.GetSongDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SongsViewModel @Inject constructor(
    private val getSongsUseCase: GetSongsUseCase,
    private val getSongDetailUseCase: GetSongDetailUseCase
) : ViewModel() {

    // State cho danh sách bài hát
    private val _songs = MutableStateFlow<List<Song>?>(null)
    val songs: StateFlow<List<Song>?> = _songs

    // State cho chi tiết bài hát
    private val _songDetail = MutableStateFlow<SongDetail?>(null)
    val songDetail: StateFlow<SongDetail?> = _songDetail

    // State cho thông báo (ví dụ lỗi hoặc message khác)
    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    // Lấy danh sách bài hát
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
}
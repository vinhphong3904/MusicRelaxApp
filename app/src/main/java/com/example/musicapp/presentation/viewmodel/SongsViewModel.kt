package com.example.musicapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapp.domain.model.Song
import com.example.musicapp.domain.usecase.GetSongsUseCase
import com.example.musicapp.domain.usecase.GetSongDetailUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SongsViewModel @Inject constructor(
    private val getSongsUseCase: GetSongsUseCase,
    private val getSongDetailUseCase: GetSongDetailUseCase
) : ViewModel() {

    private val _songs = MutableStateFlow<List<Song>>(emptyList())
    val songs: StateFlow<List<Song>> = _songs

    private val _songDetail = MutableStateFlow<Song?>(null)
    val songDetail: StateFlow<Song?> = _songDetail

    fun loadSongs(keyword: String? = null, genreId: Int? = null, artistId: Int? = null, page: Int = 1, limit: Int = 20) {
        viewModelScope.launch {
            val result = getSongsUseCase(keyword, genreId, artistId, page, limit)
            _songs.value = result
        }
    }

    fun loadSongDetail(id: Int) {
        viewModelScope.launch {
            val result = getSongDetailUseCase(id)
            _songDetail.value = result
        }
    }
}

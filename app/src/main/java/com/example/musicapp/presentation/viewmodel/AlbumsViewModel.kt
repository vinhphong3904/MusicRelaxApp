package com.example.musicapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapp.domain.model.Album
import com.example.musicapp.domain.usecase.GetAlbumsUseCase
import com.example.musicapp.domain.usecase.GetAlbumDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlbumsViewModel @Inject constructor(
    private val getAlbumsUseCase: GetAlbumsUseCase,
    private val getAlbumDetailUseCase: GetAlbumDetailUseCase
) : ViewModel() {

    // StateFlow cho danh sách album
    private val _albums = MutableStateFlow(emptyList<Album>())
    val albums: StateFlow<List<Album>> = _albums

    // StateFlow cho chi tiết album
    private val _albumDetail = MutableStateFlow<Album?>(null)
    val albumDetail: StateFlow<Album?> = _albumDetail

    fun loadAlbums(token: String, keyword: String? = null, artistId: Int? = null, page: Int = 1, limit: Int = 20) =
        viewModelScope.launch {
            _albums.value = getAlbumsUseCase(token, keyword, artistId, page, limit)
        }

    fun loadAlbumDetail(token: String, id: Int) =
        viewModelScope.launch {
            _albumDetail.value = getAlbumDetailUseCase(token, id)
        }
}


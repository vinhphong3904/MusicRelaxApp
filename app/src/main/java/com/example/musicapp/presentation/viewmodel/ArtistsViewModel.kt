package com.example.musicapp.presentation.artists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapp.domain.model.Artist
import com.example.musicapp.domain.usecase.GetArtistsUseCase
import com.example.musicapp.domain.usecase.GetArtistDetailUseCase
import com.example.musicapp.data.model.dto.ArtistDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArtistsViewModel @Inject constructor(
    private val getArtistsUseCase: GetArtistsUseCase,
    private val getArtistDetailUseCase: GetArtistDetailUseCase
) : ViewModel() {

    private val _artists = MutableStateFlow<List<Artist>>(emptyList())
    val artists: StateFlow<List<Artist>> = _artists

    private val _artistDetail = MutableStateFlow<Artist?>(null)
    val artistDetail: StateFlow<Artist?> = _artistDetail

    fun loadArtists(token: String, keyword: String? = null) {
        viewModelScope.launch {
            _artists.value = getArtistsUseCase(token, keyword)
        }
    }

    fun loadArtistDetail(token: String, id: Int) {
        viewModelScope.launch {
            _artistDetail.value = getArtistDetailUseCase(token, id)
        }
    }
}

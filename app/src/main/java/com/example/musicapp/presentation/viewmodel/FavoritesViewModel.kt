package com.example.musicapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapp.domain.usecase.*
import com.example.musicapp.data.model.dto.FavoriteDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val getFavoritesUseCase: GetFavoritesUseCase,
    private val addFavoriteUseCase: AddFavoriteUseCase,
    private val removeFavoriteUseCase: RemoveFavoriteUseCase
) : ViewModel() {

    private val _favorites = MutableStateFlow<List<FavoriteDto>>(emptyList())
    val favorites: StateFlow<List<FavoriteDto>> = _favorites

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    fun loadFavorites(token: String) {
        viewModelScope.launch {
            _favorites.value = getFavoritesUseCase(token)
        }
    }

    fun addFavorite(token: String, songId: Int) {
        viewModelScope.launch {
            _message.value = addFavoriteUseCase(token, songId)
        }
    }

    fun removeFavorite(token: String, songId: Int) {
        viewModelScope.launch {
            _message.value = removeFavoriteUseCase(token, songId)
        }
    }
}

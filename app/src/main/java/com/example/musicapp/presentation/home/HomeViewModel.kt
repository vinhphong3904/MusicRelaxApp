package com.example.musicapp.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapp.data.api.MusicApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val api: MusicApi
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadHome()
    }

    fun loadHome() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val me = runCatching { api.getMe().user }.getOrNull()

                val top = api.getTopSongs()
                val recommend = api.getRecommendSongs()

                _uiState.value = HomeUiState(
                    isLoading = false,
                    user = me,
                    topSongs = top.data,
                    recommendSongs = recommend.data
                )

            } catch (e: Exception) {
                _uiState.value = HomeUiState(
                    isLoading = false,
                    isApiError = true,
                    error = e.message ?: "Lỗi không thể tải dữ liệu!"
                )
            }
        }
    }
}




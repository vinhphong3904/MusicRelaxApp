package com.example.musicapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapp.data.model.dto.*
import com.example.musicapp.data.repository.AuthRepositoryInterface
import com.example.musicapp.domain.usecase.SearchUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchUseCase: SearchUseCase,
    private val authRepository: AuthRepositoryInterface
) : ViewModel() {

    // ===== TOKEN =====
    private val tokenState: StateFlow<String?> =
        authRepository.tokenFlow.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    // ===== UI STATE =====
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _artists = MutableStateFlow<List<SearchArtistDto>>(emptyList())
    val artists: StateFlow<List<SearchArtistDto>> = _artists

    private val _albums = MutableStateFlow<List<SearchAlbumDto>>(emptyList())
    val albums: StateFlow<List<SearchAlbumDto>> = _albums

    private val _songs = MutableStateFlow<List<SearchSongDto>>(emptyList())
    val songs: StateFlow<List<SearchSongDto>> = _songs

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    // ===== PAGINATION =====
    private var currentPage = 1
    private var currentKeyword: String = ""
    private var hasMore = true

    init {
        // Token đổi → search lại nếu đang có keyword
        tokenState
            .filterNotNull()
            .onEach {
                if (currentKeyword.isNotEmpty()) {
                    search(currentKeyword)
                }
            }
            .launchIn(viewModelScope)
    }

    // ===== SEARCH =====
    fun search(keyword: String, page: Int = 1, limit: Int = 10) {
        val token = tokenState.value
        if (token.isNullOrEmpty()) {
            _message.value = "User chưa đăng nhập"
            return
        }

        viewModelScope.launch {
            try {
                _isLoading.value = true
                currentKeyword = keyword
                currentPage = page

                val response = searchUseCase(token, keyword, page, limit)

                _artists.value = response.data.artists
                _albums.value = response.data.albums
                _songs.value = response.data.songs

                hasMore = response.data.songs.size == limit
                _message.value = null
            } catch (e: Exception) {
                hasMore = false
                _message.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ===== LOAD MORE =====
    fun loadMore(limit: Int = 10) {
        val token = tokenState.value ?: return
        if (!hasMore || _isLoading.value) return

        viewModelScope.launch {
            try {
                _isLoading.value = true
                val nextPage = currentPage + 1

                val response = searchUseCase(token, currentKeyword, nextPage, limit)

                _artists.value += response.data.artists
                _albums.value += response.data.albums
                _songs.value += response.data.songs

                currentPage = nextPage
                hasMore = response.data.songs.size == limit
            } catch (e: Exception) {
                hasMore = false
                _message.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
}

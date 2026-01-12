package com.example.musicapp.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapp.domain.repository.PlaylistRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel cho Home Screen
 * 
 * Nhiệm vụ:
 * - Load songs từ repository
 * - Load playlists của user
 * - Combine 2 flows → 1 UiState
 * - Handle search
 */
//@HiltViewModel
//class HomeViewModel @Inject constructor(
//    private val songRepository: SongRepository,
//    private val playlistRepository: PlaylistRepository
//) : ViewModel() {
//
//    /**
//     * Search query state
//     * User gõ vào search bar → update query này
//     */
//    private val _searchQuery = MutableStateFlow("")
//    val searchQuery = _searchQuery.asStateFlow()
//
//    /**
//     * UI State chính
//     *
//     * Combine 3 flows:
//     * 1. Songs flow
//     * 2. Playlists flow
//     * 3. Search query flow
//     *
//     * → Emit HomeUiState khi bất kỳ flow nào thay đổi
//     */
//    val uiState: StateFlow<HomeUiState> = combine(
//        songRepository.getSongs(),      // Flow 1
//        playlistRepository.getPlaylists(),  // Flow 2 (giả sử có)
//        _searchQuery                    // Flow 3
//    ) { songs, playlists, query ->
//
//        // Filter songs theo search query
//        val filteredSongs = if (query.isBlank()) {
//            songs
//        } else {
//            songs.filter {
//                it.title.contains(query, ignoreCase = true) ||
//                it.artist.contains(query, ignoreCase = true)
//            }
//        }
//
//        // Return combined state
//        HomeUiState(
//            songs = filteredSongs,
//            playlists = playlists,
//            isLoading = false,
//            errorMessage = null
//        )
//
//    }.catch { exception ->
//        // Handle error from any flow
//        emit(
//            HomeUiState(
//                isLoading = false,
//                errorMessage = exception.message ?: "Có lỗi xảy ra"
//            )
//        )
//    }.stateIn(
//        scope = viewModelScope,
//        started = SharingStarted.WhileSubscribed(5000), // Stop sau 5s không có subscriber
//        initialValue = HomeUiState(isLoading = true)
//    )
//
//    /**
//     * Update search query
//     * Gọi từ UI khi user gõ vào search bar
//     *
//     * @param query: Từ khóa tìm kiếm
//     */
//    fun onSearchQueryChange(query: String) {
//        _searchQuery.value = query
//    }
//
//    /**
//     * Refresh data
//     * Pull-to-refresh hoặc retry khi error
//     */
//    fun refresh() {
//        viewModelScope.launch {
//            // Repository tự fetch lại từ API
//            // Flow sẽ auto emit data mới
//        }
//    }
//}
//@HiltViewModel
//class HomeViewModel @Inject constructor() : ViewModel() {
//
//    // State chứa danh sách bài hát và trạng thái loading
//    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
//    val uiState = _uiState.asStateFlow()
//
//    init {
//        loadSongs()
//    }
//
//    fun loadSongs() {
//        viewModelScope.launch {
//            // 1. Báo đang load
//            _uiState.value = HomeUiState.Loading
//
//            // 2. Giả vờ đợi 2 giây (giống như đang gọi API thật)
//            delay(2000)
//
//            // 3. Tạo dữ liệu giả
//            val fakeSongs = listOf(
//                Song("1", "Chúng ta của tương lai", "Sơn Tùng M-TP", ""),
//                Song("2", "Nâng chén tiêu sầu", "Bích Phương", ""),
//                Song("3", "Thiên Lý Ơi", "Jack - J97", ""),
//                Song("4", "Sau lời từ khước", "Phan Mạnh Quỳnh", ""),
//                Song("5", "Một vòng Việt Nam", "Tùng Dương", ""),
//                Song("6", "Cắt đôi nỗi sầu", "Tăng Duy Tân", ""),
//            )
//
//            // 4. Trả về kết quả thành công
//            _uiState.value = HomeUiState.Success(fakeSongs)
//        }
//    }
//}
//
//// Định nghĩa các trạng thái của màn hình
//sealed class HomeUiState {
//    object Loading : HomeUiState()
//    data class Success(val songs: List<Song>) : HomeUiState()
//    data class Error(val message: String) : HomeUiState()
//}
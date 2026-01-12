package com.example.musicapp.presentation.player
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.example.musicapp.core.player.MusicServiceConnection
//import com.example.musicapp.core.player.PlaybackState
//import com.example.musicapp.domain.model.Song
//import dagger.hilt.android.lifecycle.HiltViewModel
//import kotlinx.coroutines.delay
//import kotlinx.coroutines.flow.*
//import kotlinx.coroutines.launch
//import javax.inject.Inject
//
///**
// * ViewModel cho Player Screen
// *
// * Nhiệm vụ:
// * - Bridge giữa UI và MusicService
// * - Expose player state (current song, isPlaying, position...)
// * - Handle user actions (play/pause, seek, next/prev)
// */
//@HiltViewModel
//class PlayerViewModel @Inject constructor(
//    private val musicServiceConnection: MusicServiceConnection
//) : ViewModel() {
//
//    /**
//     * Current song (từ service)
//     */
//    val currentSong: StateFlow<Song?> = musicServiceConnection.currentSong
//
//    /**
//     * Playback state (từ service)
//     */
//    val playbackState: StateFlow<PlaybackState> = musicServiceConnection.playbackState
//
//    /**
//     * Current position
//     * Update mỗi 100ms để slider smooth
//     */
//    val currentPosition: StateFlow<Long> = flow {
//        while (true) {
//            emit(musicServiceConnection.getCurrentPosition())
//            delay(100) // Update 10 lần/giây
//        }
//    }.stateIn(
//        scope = viewModelScope,
//        started = SharingStarted.WhileSubscribed(5000),
//        initialValue = 0L
//    )
//
//    /**
//     * Computed: isPlaying
//     */
//    val isPlaying: StateFlow<Boolean> = playbackState.map { state ->
//        state is PlaybackState.Playing
//    }.stateIn(viewModelScope, SharingStarted.Eagerly, false)
//
//    /**
//     * UI State tổng hợp
//     */
//    val uiState: StateFlow<PlayerUiState> = combine(
//        currentSong,
//        isPlaying,
//        currentPosition,
//        playbackState
//    ) { song, playing, position, state ->
//        PlayerUiState(
//            currentSong = song,
//            isPlaying = playing,
//            currentPosition = position,
//            isBuffering = state is PlaybackState.Buffering
//        )
//    }.stateIn(
//        scope = viewModelScope,
//        started = SharingStarted.WhileSubscribed(5000),
//        initialValue = PlayerUiState()
//    )
//
//    /**
//     * Play một bài hát
//     */
//    fun playSong(song: Song) {
//        musicServiceConnection.playSong(song)
//    }
//
//    /**
//     * Toggle play/pause
//     */
//    fun togglePlayPause() {
//        musicServiceConnection.togglePlayPause()
//    }
//
//    /**
//     * Seek to position
//     *
//     * @param positionMs: Vị trí mới (milliseconds)
//     */
//    fun seekTo(positionMs: Long) {
//        musicServiceConnection.seekTo(positionMs)
//    }
//
//    /**
//     * Skip to next song
//     */
//    fun skipToNext() {
//        // TODO: Implement queue logic
//    }
//
//    /**
//     * Skip to previous
//     */
//    fun skipToPrevious() {
//        // TODO: Implement queue logic
//    }
//}
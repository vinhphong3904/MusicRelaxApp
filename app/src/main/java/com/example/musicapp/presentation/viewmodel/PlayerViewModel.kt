package com.example.musicapp.presentation.viewmodel

import android.content.ComponentName
import android.content.Context
import androidx.annotation.OptIn
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.musicapp.domain.model.Song
import com.example.musicapp.domain.usecase.GetSongDetailUseCase
import com.example.musicapp.domain.usecase.GetSongsUseCase
import com.example.musicapp.service.MusicService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.musicapp.data.mapper.toDomain

@HiltViewModel
class PlayerViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getSongsUseCase: GetSongsUseCase,
    private val getSongDetailUseCase: GetSongDetailUseCase
) : ViewModel() {

    // ================= MEDIA CONTROLLER =================
    private var controller: MediaController? = null

    // ================= UI STATES =================
    private val _currentSong = MutableStateFlow<Song?>(null)
    val currentSong: StateFlow<Song?> = _currentSong.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _progress = MutableStateFlow(0f)
    val progress: StateFlow<Float> = _progress.asStateFlow()

    private val _playlist = MutableStateFlow<List<Song>>(emptyList())
    val playlist: StateFlow<List<Song>> = _playlist.asStateFlow()

    private val _currentIndex = MutableStateFlow(0)

    val currentIndex: StateFlow<Int> = _currentIndex.asStateFlow()
    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long> = _currentPosition.asStateFlow()

    private val _duration = MutableStateFlow(0L)
    val duration: StateFlow<Long> = _duration.asStateFlow()

    // ===== MESSAGE =====
    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()



    // ================= INIT =================
    init {
        connectToMusicService()
        observeProgress()
        loadSongs()
    }

    // ================= CONNECT SERVICE =================
    @OptIn(UnstableApi::class)
    private fun connectToMusicService() {
        val sessionToken = SessionToken(
            context,
            ComponentName(context, MusicService::class.java)
        )

        val controllerFuture =
            MediaController.Builder(context, sessionToken).buildAsync()

        controllerFuture.addListener({
            controller = controllerFuture.get()
            observePlayerState()
        }, ContextCompat.getMainExecutor(context))
    }

    // ================= PLAYER LISTENER =================
    private fun observePlayerState() {
        controller?.addListener(object : Player.Listener {

            override fun onMediaItemTransition(
                mediaItem: MediaItem?,
                reason: Int
            ) {
                val index = controller?.currentMediaItemIndex ?: 0
                _currentIndex.value = index
                _currentSong.value = _playlist.value.getOrNull(index)
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _isPlaying.value = isPlaying
            }
        })
    }

    // ================= PROGRESS =================
    private fun observeProgress() {
        viewModelScope.launch {
            while (true) {
                controller?.let {
                    _currentPosition.value = it.currentPosition
                    _duration.value = maxOf(it.duration, 0L)

                    if (it.duration > 0) {
                        _progress.value =
                            it.currentPosition.toFloat() / it.duration
                    }
                }
                delay(500)
            }
        }
    }


    // ================= DATA =================
    fun loadSongs(page: Int = 1, limit: Int = 20) {
        viewModelScope.launch {
            getSongsUseCase(page, limit).collect { result: Result<List<Song>> ->
                result.onSuccess { songs: List<Song> ->
                    _playlist.value = songs
                    if (songs.isNotEmpty() && _currentSong.value == null) {
                        setPlaylist(songs, 0)
                    }
                }
                result.onFailure { e: Throwable ->
                    _message.value = e.message
                }
            }
        }
    }

    fun loadSongDetail(songId: Int) {
        viewModelScope.launch {
            getSongDetailUseCase(songId).collect { result: Result<com.example.musicapp.domain.model.SongDetail> ->
                result.onSuccess { detail: com.example.musicapp.domain.model.SongDetail ->
                    // Nếu player dùng Song, convert từ SongDetail sang Song
                    val song = Song(
                        id = detail.id,
                        title = detail.title,
                        duration = detail.duration,
                        audioUrl = detail.audioUrl,
                        coverImageUrl = detail.albumCover ?: detail.coverImageUrl,
                        viewCount = detail.viewCount,
                        slug = detail.slug,
                        artistId = detail.artistId,
                        artistName = detail.artistName,
                        genreId = detail.genreId,
                        genreName = detail.genreName
                    )
                    _currentSong.value = song
                    _playlist.value = listOf(song)
                    setPlaylist(listOf(song), 0)
                }
                result.onFailure { e: Throwable ->
                    _message.value = e.message
                }
            }
        }
    }






    // ================= PLAYBACK =================
    fun setPlaylist(songs: List<Song>, startIndex: Int = 0) {
        _playlist.value = songs

        val mediaItems = songs.map {
            MediaItem.Builder()
                .setMediaId(it.id.toString())
                .setUri(it.audioUrl)
                .setMediaMetadata(
                    androidx.media3.common.MediaMetadata.Builder()
                        .setTitle(it.title)
                        .setArtist(it.artistName)
                        .build()
                )
                .build()
        }

        controller?.apply {
            setMediaItems(mediaItems, startIndex, 0L)
            prepare()
            play()
        }
    }

    fun playSong(song: Song) {
        val index = _playlist.value.indexOfFirst { it.id == song.id }
        if (index != -1) {
            controller?.seekTo(index, 0L)
            controller?.play()
        }
    }

    fun togglePlayPause() {
        controller?.let {
            if (it.isPlaying) it.pause() else it.play()
        }
    }

    fun skipToNext() {
        controller?.seekToNext()
    }

    fun skipToPrevious() {
        controller?.seekToPrevious()
    }

    fun seekTo(progress: Float) {
        controller?.let {
            if (it.duration > 0) {
                it.seekTo((progress * it.duration).toLong())
                _progress.value = progress
            }
        }
    }

    fun formatTime(ms: Long): String {
        val totalSeconds = ms / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return "%02d:%02d".format(minutes, seconds)
    }

    // ================= CLEAN =================
    override fun onCleared() {
        super.onCleared()
        controller?.release()
        controller = null
    }
}

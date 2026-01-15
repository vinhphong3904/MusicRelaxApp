package com.example.musicapp.service

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.example.musicapp.domain.model.Song
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@AndroidEntryPoint
class MusicService : MediaSessionService() {

    private var mediaSession: MediaSession? = null
    private lateinit var player: ExoPlayer

    private val _currentSong = MutableStateFlow<Song?>(null)
    val currentSong: StateFlow<Song?> = _currentSong.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long> = _currentPosition.asStateFlow()

    private val _playlist = MutableStateFlow<List<Song>>(emptyList())
    val playlist: StateFlow<List<Song>> = _playlist.asStateFlow()

    private val _currentIndex = MutableStateFlow(0)
    val currentIndex: StateFlow<Int> = _currentIndex.asStateFlow()

    private val binder = MusicBinder()

    inner class MusicBinder : Binder() {
        fun getService(): MusicService = this@MusicService
    }

    override fun onCreate() {
        super.onCreate()

        player = ExoPlayer.Builder(this).build()

        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _isPlaying.value = isPlaying
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == Player.STATE_ENDED) {
                    skipToNext()
                }
            }

            override fun onPositionDiscontinuity(
                oldPosition: Player.PositionInfo,
                newPosition: Player.PositionInfo,
                reason: Int
            ) {
                _currentPosition.value = newPosition.positionMs
            }
        })

        val sessionActivityIntent = packageManager?.getLaunchIntentForPackage(packageName)
        val sessionActivityPendingIntent = PendingIntent.getActivity(
            this,
            0,
            sessionActivityIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        mediaSession = MediaSession.Builder(this, player)
            .setSessionActivity(sessionActivityPendingIntent)
            .build()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    override fun onBind(intent: Intent?): IBinder? {
        super.onBind(intent)
        return binder
    }

    fun setPlaylist(songs: List<Song>, startIndex: Int = 0) {
        _playlist.value = songs
        _currentIndex.value = startIndex

        player.clearMediaItems()
        songs.forEach { song ->
            val mediaItem = MediaItem.Builder()
                .setUri(song.audioUrl)
                .setMediaId(song.id.toString())
                .build()
            player.addMediaItem(mediaItem)
        }

        player.seekTo(startIndex, 0)
        player.prepare()
        _currentSong.value = songs.getOrNull(startIndex)
    }

    fun playSong(song: Song) {
        val index = _playlist.value.indexOfFirst { it.id == song.id }
        if (index != -1) {
            player.seekTo(index, 0)
            player.play()
            _currentIndex.value = index
            _currentSong.value = song
        }
    }

    fun play() {
        player.play()
    }

    fun pause() {
        player.pause()
    }

    fun togglePlayPause() {
        if (player.isPlaying) {
            pause()
        } else {
            play()
        }
    }

    fun skipToNext() {
        val nextIndex = (_currentIndex.value + 1) % _playlist.value.size
        player.seekTo(nextIndex, 0)
        player.play()
        _currentIndex.value = nextIndex
        _currentSong.value = _playlist.value.getOrNull(nextIndex)
    }

    fun skipToPrevious() {
        val prevIndex = if (_currentIndex.value > 0) {
            _currentIndex.value - 1
        } else {
            _playlist.value.size - 1
        }
        player.seekTo(prevIndex, 0)
        player.play()
        _currentIndex.value = prevIndex
        _currentSong.value = _playlist.value.getOrNull(prevIndex)
    }

    fun seekTo(position: Long) {
        player.seekTo(position)
        _currentPosition.value = position
    }

    fun getCurrentProgress(): Float {
        if (player.duration <= 0) return 0f
        return player.currentPosition.toFloat() / player.duration.toFloat()
    }

    override fun onDestroy() {
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        super.onDestroy()
    }
}
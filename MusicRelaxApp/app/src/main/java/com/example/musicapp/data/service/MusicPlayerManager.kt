package com.example.musicapp.data.service

import android.content.Context
import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object MusicPlayerManager {
    private var exoPlayer: ExoPlayer? = null
    
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying = _isPlaying.asStateFlow()

    private val _progress = MutableStateFlow(0f)
    val progress = _progress.asStateFlow()

    fun init(context: Context) {
        if (exoPlayer == null) {
            exoPlayer = ExoPlayer.Builder(context).build().apply {
                repeatMode = Player.REPEAT_MODE_ALL
                addListener(object : Player.Listener {
                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        _isPlaying.value = isPlaying
                        Log.d("MusicPlayer", "Is playing: $isPlaying")
                    }

                    override fun onPlayerError(error: PlaybackException) {
                        super.onPlayerError(error)
                        Log.e("MusicPlayer", "Error playing music: ${error.message}")
                        Log.e("MusicPlayer", "Cause: ${error.cause}")
                    }

                    override fun onPlaybackStateChanged(state: Int) {
                        when(state) {
                            Player.STATE_READY -> Log.d("MusicPlayer", "Player is Ready")
                            Player.STATE_BUFFERING -> Log.d("MusicPlayer", "Player is Buffering")
                            Player.STATE_ENDED -> Log.d("MusicPlayer", "Player ended")
                            Player.STATE_IDLE -> Log.d("MusicPlayer", "Player is Idle")
                        }
                    }
                })
            }
        }
    }

    fun play(url: String) {
        // Chuyển đổi localhost sang IP máy ảo Android chuẩn
        val finalUrl = url.replace("localhost", "10.0.2.2")
        Log.d("MusicPlayer", "Attempting to play: $finalUrl")
        
        exoPlayer?.apply {
            stop() // Dừng bài cũ
            clearMediaItems()
            setMediaItem(MediaItem.fromUri(finalUrl))
            prepare()
            play()
        }
    }

    fun togglePlayPause() {
        exoPlayer?.let {
            if (it.isPlaying) it.pause() else it.play()
        }
    }

    fun seekTo(position: Float) {
        exoPlayer?.let {
            val duration = it.duration
            if (duration > 0) {
                it.seekTo((position * duration).toLong())
            }
        }
    }

    fun updateProgress() {
        exoPlayer?.let {
            if (it.duration > 0) {
                _progress.value = it.currentPosition.toFloat() / it.duration
            }
        }
    }
}

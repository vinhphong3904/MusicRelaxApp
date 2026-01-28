package com.example.musicapp.data.service

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.musicapp.core.player.MusicService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object MusicPlayerManager {
    private var exoPlayer: ExoPlayer? = null
    
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying = _isPlaying.asStateFlow()

    private val _progress = MutableStateFlow(0f)
    val progress = _progress.asStateFlow()

    fun getOrCreatePlayer(context: Context): ExoPlayer {
        if (exoPlayer == null) {
            exoPlayer = ExoPlayer.Builder(context.applicationContext).build().apply {
                val audioAttributes = AudioAttributes.Builder()
                    .setUsage(C.USAGE_MEDIA)
                    .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                    .build()
                setAudioAttributes(audioAttributes, true)
                setWakeMode(C.WAKE_MODE_NETWORK) 
                
                repeatMode = Player.REPEAT_MODE_ALL
                addListener(object : Player.Listener {
                    override fun onIsPlayingChanged(playing: Boolean) {
                        _isPlaying.value = playing
                    }
                    override fun onPlayerError(error: PlaybackException) {
                        Log.e("MusicPlayer", "Lỗi: ${error.message}")
                    }
                })
            }
        }
        return exoPlayer!!
    }

    fun getPlayer(): ExoPlayer? = exoPlayer

    fun play(context: Context, url: String, title: String, artist: String) {
        val player = getOrCreatePlayer(context)
        
        val metadata = MediaMetadata.Builder()
            .setTitle(title)
            .setArtist(artist)
            .build()

        val mediaItem = MediaItem.Builder()
            .setUri(url)
            .setMediaMetadata(metadata)
            .build()

        player.apply {
            stop()
            clearMediaItems()
            setMediaItem(mediaItem)
            prepare()
            play()
        }

        // Chỉ khởi động Service thông thường, để MediaSessionService tự quản lý Notification
        val intent = Intent(context.applicationContext, MusicService::class.java)
        context.startService(intent)
    }

    fun togglePlayPause() {
        exoPlayer?.let { if (it.isPlaying) it.pause() else it.play() }
    }

    fun seekTo(position: Float) {
        exoPlayer?.let {
            if (it.duration > 0) it.seekTo((position * it.duration).toLong())
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

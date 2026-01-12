package com.example.musicapp.core.player
//
//import android.app.PendingIntent
//import android.content.Intent
//import androidx.media3.common.MediaItem
//import androidx.media3.common.Player
//import androidx.media3.exoplayer.ExoPlayer
//import androidx.media3.session.MediaSession
//import androidx.media3.session.MediaSessionService
//import dagger.hilt.android.AndroidEntryPoint
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import javax.inject.Inject
//
///**
// * Foreground Service quản lý ExoPlayer
// * Chạy background → nhạc phát tiếp khi minimize app
// *
// * Extends MediaSessionService:
// * - Tích hợp Media3 MediaSession
// * - Hiện notification với controls (play/pause/skip)
// * - Support Android Auto, Wear OS
// *
// * @AndroidEntryPoint: Hilt inject dependencies
// */
//@AndroidEntryPoint
//class MusicService : MediaSessionService() {
//
//    /**
//     * ExoPlayer instance (inject từ Hilt)
//     */
//    @Inject
//    lateinit var player: ExoPlayer
//
//    /**
//     * MediaSession để control player
//     */
//    private var mediaSession: MediaSession? = null
//
//    /**
//     * Current song state
//     */
//    private val _currentSong = MutableStateFlow<Song?>(null)
//    val currentSong: StateFlow<Song?> = _currentSong.asStateFlow()
//
//    /**
//     * Playback state
//     */
//    private val _playbackState = MutableStateFlow<PlaybackState>(PlaybackState.Idle)
//    val playbackState: StateFlow<PlaybackState> = _playbackState.asStateFlow()
//
//    /**
//     * Current position (milliseconds)
//     */
//    private val _currentPosition = MutableStateFlow(0L)
//    val currentPosition: StateFlow<Long> = _currentPosition.asStateFlow()
//
//    /**
//     * onCreate: Setup player và session
//     */
//    override fun onCreate() {
//        super.onCreate()
//
//        /**
//         * Setup ExoPlayer listeners
//         */
//        player.addListener(object : Player.Listener {
//            /**
//             * Callback khi playback state thay đổi
//             *
//             * ExoPlayer states:
//             * - STATE_IDLE: Chưa load media
//             * - STATE_BUFFERING: Đang load
//             * - STATE_READY: Sẵn sàng play
//             * - STATE_ENDED: Hết bài
//             */
//            override fun onPlaybackStateChanged(state: Int) {
//                _playbackState.value = when (state) {
//                    Player.STATE_IDLE -> PlaybackState.Idle
//                    Player.STATE_BUFFERING -> PlaybackState.Buffering
//                    Player.STATE_READY -> {
//                        if (player.isPlaying) PlaybackState.Playing
//                        else PlaybackState.Paused
//                    }
//                    Player.STATE_ENDED -> PlaybackState.Paused
//                    else -> PlaybackState.Idle
//                }
//            }
//
//            /**
//             * Callback khi play/pause
//             */
//            override fun onIsPlayingChanged(isPlaying: Boolean) {
//                if (isPlaying) {
//                    _playbackState.value = PlaybackState.Playing
//                } else {
//                    _playbackState.value = PlaybackState.Paused
//                }
//            }
//
//            /**
//             * Callback khi có lỗi
//             */
//            override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
//                _playbackState.value = PlaybackState.Error(
//                    error.message ?: "Playback error"
//                )
//            }
//        })
//
//        /**
//         * Create MediaSession
//         * SessionActivity: Activity mở khi click notification
//         */
//        val sessionActivityIntent = packageManager.getLaunchIntentForPackage(packageName)
//        val sessionActivityPendingIntent = PendingIntent.getActivity(
//            this,
//            0,
//            sessionActivityIntent,
//            PendingIntent.FLAG_IMMUTABLE
//        )
//
//        mediaSession = MediaSession.Builder(this, player)
//            .setSessionActivity(sessionActivityPendingIntent)
//            .build()
//    }
//
//    /**
//     * Return MediaSession cho system
//     * Required override
//     */
//    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
//        return mediaSession
//    }
//
//    /**
//     * Play một bài hát
//     *
//     * @param song: Song cần phát
//     */
//    fun playSong(song: Song) {
//        _currentSong.value = song
//
//        // Create MediaItem từ URL
//        val mediaItem = MediaItem.Builder()
//            .setUri(song.audioUrl)
//            .setMediaId(song.id)
//            .build()
//
//        // Set media và play
//        player.setMediaItem(mediaItem)
//        player.prepare()  // Load audio
//        player.play()     // Start playback
//    }
//
//    /**
//     * Play/Pause toggle
//     */
//    fun togglePlayPause() {
//        if (player.isPlaying) {
//            player.pause()
//        } else {
//            player.play()
//        }
//    }
//
//    /**
//     * Seek to position
//     *
//     * @param positionMs: Vị trí cần seek (milliseconds)
//     */
//    fun seekTo(positionMs: Long) {
//        player.seekTo(positionMs)
//    }
//
//    /**
//     * Get current position
//     */
//    fun getCurrentPosition(): Long {
//        return player.currentPosition
//    }
//
//    /**
//     * Skip to next (nếu có queue)
//     */
//    fun skipToNext() {
//        if (player.hasNextMediaItem()) {
//            player.seekToNext()
//        }
//    }
//
//    /**
//     * Skip to previous
//     */
//    fun skipToPrevious() {
//        if (player.hasPreviousMediaItem()) {
//            player.seekToPrevious()
//        }
//    }
//
//    /**
//     * onDestroy: Release resources
//     */
//    override fun onDestroy() {
//        // Release MediaSession
//        mediaSession?.run {
//            player.release()
//            release()
//            mediaSession = null
//        }
//        super.onDestroy()
//    }
//}
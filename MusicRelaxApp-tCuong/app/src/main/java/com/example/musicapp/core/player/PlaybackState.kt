package com.example.musicapp.core.player
//
///**
// * Sealed class cho playback states
// * Tương ứng với ExoPlayer.STATE_*
// */
//sealed class PlaybackState {
//    /**
//     * Idle - Chưa load bài hát nào
//     */
//    object Idle : PlaybackState()
//
//    /**
//     * Buffering - Đang load audio
//     */
//    object Buffering : PlaybackState()
//
//    /**
//     * Ready - Đã load xong, sẵn sàng play
//     */
//    object Ready : PlaybackState()
//
//    /**
//     * Playing - Đang phát nhạc
//     */
//    object Playing : PlaybackState()
//
//    /**
//     * Paused - Đã tạm dừng
//     */
//    object Paused : PlaybackState()
//
//    /**
//     * Error - Có lỗi xảy ra
//     */
//    data class Error(val message: String) : PlaybackState()
//}
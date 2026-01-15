package com.example.musicapp.core.common

/**
 * Constants cho toàn app
 */
object Constants {
    // ============ API CONFIG ============
    const val BASE_URL = "http://192.168.43.50:8080/"
    const val NETWORK_TIMEOUT = 30L

    // ============ SHARED PREFERENCES ============
    const val PREF_AUTH_TOKEN = "auth_token"
    const val PREF_USER_ID = "user_id"
    
    // ============ DATABASE ============
    const val DATABASE_NAME = "music_app_database"
    const val DATABASE_VERSION = 1
    
    // ============ NOTIFICATION ============
    const val NOTIFICATION_ID = 1001
    const val NOTIFICATION_CHANNEL_ID = "music_player_channel"
    
    // ============ PAGINATION ============
    const val PAGE_SIZE = 20
}
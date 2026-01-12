package com.example.musicapp.presentation.navigation

/**
 * Sealed class định nghĩa tất cả routes
 * Type-safe navigation
 */
sealed class Screen(val route: String) {
    /**
     * Auth routes
     */
    object Login : Screen("login")
    object Register : Screen("register")
    
    /**
     * Main routes
     */
    object Home : Screen("home")
    object Player : Screen("player")
    object Library : Screen("library")
    object Search : Screen("search")
    
    /**
     * Detail routes (có parameters)
     */
    object PlaylistDetail : Screen("playlist/{playlistId}") {
        fun createRoute(playlistId: String) = "playlist/$playlistId"
    }
    
    /**
     * Admin routes
     */
    object Admin : Screen("admin")
}
package com.example.musicapp.presentation.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object Player : Screen("player")
    object Library : Screen("library")
    object Search : Screen("search")
    object Playlist : Screen("playlist")
    object Profile : Screen("profile")
    
    object PlaylistDetail : Screen("playlist/{playlistId}") {
        fun createRoute(playlistId: String) = "playlist/$playlistId"
    }
    
    object Admin : Screen("admin")
}
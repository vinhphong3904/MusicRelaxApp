package com.example.musicapp.presentation.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object Library : Screen("library")
    object Playlist : Screen("playlist")
    object Profile : Screen("profile")
    object Search : Screen("search")
    object Player : Screen("player/{title}/{artist}/{icon}") {
        fun createRoute(title: String, artist: String, icon: Int): String {
            return "player/$title/$artist/$icon"
        }
    }


    object PlaylistDetail : Screen("playlist/{playlistId}") {
        fun createRoute(playlistId: String) = "playlist/$playlistId"
    }
    
    object Admin : Screen("admin")
}
package com.example.musicapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.musicapp.presentation.home.HomeScreen
import com.example.musicapp.presentation.library.LibraryScreen
import com.example.musicapp.presentation.library.PlaylistScreen
import com.example.musicapp.presentation.library.FavoritesScreen
import com.example.musicapp.presentation.search.SearchScreen
import com.example.musicapp.presentation.settings.ProfileScreen
import com.example.musicapp.presentation.history.HistoryScreen
import com.example.musicapp.presentation.settings.SettingsScreen
import com.example.musicapp.presentation.auth.LoginScreen
import com.example.musicapp.presentation.auth.RegisterScreen
import com.example.musicapp.presentation.home.HomeViewModel
import com.example.musicapp.presentation.player.PlayerScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = Screen.Home.route,
    currentPlayingSong: Triple<String, String, Int>?,
    isPlaying: Boolean,
    progress: Float,
    onSongSelect: (Triple<String, String, Int>) -> Unit,
    onPlayPauseChange: (Boolean) -> Unit,
    onProgressChange: (Float) -> Unit,
    onNextPrev: (Int) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(route = Screen.Home.route) {
            HomeScreen(navController = navController)
        }
        composable(route = Screen.Search.route) {
            SearchScreen(
                navController = navController,
                onSongSelect = { song -> onSongSelect(song) } // Chỉ gọi onSongSelect, việc navigate đã có MainActivity lo
            )
        }
        composable(route = Screen.Library.route) {
            LibraryScreen(navController = navController)
        }
        composable(route = Screen.Playlist.route) {
            PlaylistScreen(
                navController = navController,
                onSongSelect = { song -> onSongSelect(song) }
            )
        }
        composable(route = Screen.Favorites.route) {
            FavoritesScreen(
                navController = navController,
                onSongSelect = { song -> onSongSelect(song) }
            )
        }
        composable(route = Screen.Profile.route) {
            ProfileScreen(navController = navController)
        }
        composable(route = Screen.Login.route) {
            LoginScreen(navController = navController)
        }
        composable(route = Screen.Register.route) {
            RegisterScreen(navController = navController)
        }
        composable(route = Screen.Player.route) {
            PlayerScreen(
                navController = navController,
                currentPlayingSong = currentPlayingSong,
                isPlaying = isPlaying,
                progress = progress,
                onPlayPauseChange = onPlayPauseChange,
                onProgressChange = onProgressChange,
                onNextPrev = onNextPrev
            )
        }
        composable(route = "history") {
            HistoryScreen(navController = navController)
        }
        composable(route = "settings") {
            SettingsScreen(navController = navController)
        }
    }
}

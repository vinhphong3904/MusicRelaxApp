package com.example.musicapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.musicapp.presentation.home.HomeScreen
import com.example.musicapp.presentation.library.LibraryScreen
import com.example.musicapp.presentation.library.PlaylistScreen
import com.example.musicapp.presentation.search.SearchScreen
import com.example.musicapp.presentation.settings.ProfileScreen
import com.example.musicapp.presentation.history.HistoryScreen
import com.example.musicapp.presentation.settings.SettingsScreen
import com.example.musicapp.presentation.auth.LoginScreen
import com.example.musicapp.presentation.auth.RegisterScreen
import com.example.musicapp.presentation.player.PlayerScreen
import com.example.musicapp.presentation.viewmodel.AuthViewModel

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
            HomeScreen(
                navController = navController,
                onSongSelect = { song: Triple<String, String, Int> ->
                    onSongSelect(song)
                    navController.navigate(
                        Screen.Player.createRoute(song.first, song.second, song.third)
                    )
                }
            )
        }
        composable(route = Screen.Search.route) {
            // Lấy AuthViewModel để có token
            val authViewModel: AuthViewModel = hiltViewModel()
            val token by authViewModel.tokenFlow.collectAsState(initial = "")

            SearchScreen(
                navController = navController,
                onSongSelect = { song: Triple<String, String, Int> ->
                    navController.navigate(
                        Screen.Player.createRoute(song.first, song.second, song.third)
                    )
                },
                token = token ?: ""
            )
        }


        composable(route = Screen.Library.route) {
            LibraryScreen(navController = navController)
        }
        composable(route = Screen.Playlist.route) {
            PlaylistScreen(navController = navController)
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

package com.example.musicapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
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
import com.example.musicapp.presentation.player.PlayerScreen
import com.example.musicapp.data.model.SongDto

@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = Screen.Home.route,
    currentPlayingSong: SongDto?,
    isPlaying: Boolean,
    progress: Float,
    isFavorite: Boolean,
    onFavoriteToggle: (Boolean) -> Unit,
    onSongSelect: (SongDto) -> Unit,
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
            HomeScreen(navController = navController, onSongSelect = onSongSelect)
        }
        
        composable(route = Screen.Search.route) {
            SearchScreen(
                navController = navController,
                onSongSelect = onSongSelect // ĐÃ SỬA: Dùng trực tiếp SongDto có ID thật
            )
        }
        
        composable(route = Screen.Library.route) {
            LibraryScreen(navController = navController)
        }
        
        composable(
            route = "playlist?playlistId={playlistId}",
            arguments = listOf(navArgument("playlistId") { type = NavType.IntType; defaultValue = -1 })
        ) { backStackEntry ->
            val playlistId = backStackEntry.arguments?.getInt("playlistId") ?: -1
            PlaylistScreen(
                navController = navController,
                initialPlaylistId = if (playlistId != -1) playlistId else null,
                onSongSelect = onSongSelect
            )
        }

        composable(route = Screen.Favorites.route) {
            FavoritesScreen(
                navController = navController,
                onSongSelect = onSongSelect
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
                isFavorite = isFavorite,
                onFavoriteToggle = onFavoriteToggle,
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

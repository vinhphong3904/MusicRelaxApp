package com.example.musicapp.presentation.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = SpotifyGreen,
    background = SpotifyBlack,
    surface = SpotifyDark,
    onPrimary = SpotifyBlack,
    onBackground = SpotifyWhite,
    onSurface = SpotifyWhite
)

@Composable
fun MusicAppTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography(),
        content = content
    )
}

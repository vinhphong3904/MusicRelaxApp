package com.example.musicapp.core.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * Light theme color scheme
 */
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF1DB954),      // Spotify green
    onPrimary = Color.White,
    primaryContainer = Color(0xFF1ED760),
    onPrimaryContainer = Color.Black,
    
    secondary = Color(0xFF191414),    // Dark gray
    onSecondary = Color.White,
    
    background = Color.White,
    onBackground = Color.Black,
    
    surface = Color.White,
    onSurface = Color.Black,
    
    error = Color(0xFFB00020),
    onError = Color.White
)

/**
 * Dark theme color scheme
 */
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF1DB954),
    onPrimary = Color.White,
    primaryContainer = Color(0xFF1ED760),
    onPrimaryContainer = Color.Black,
    
    secondary = Color(0xFFB3B3B3),
    onSecondary = Color.Black,
    
    background = Color(0xFF121212),   // True black
    onBackground = Color.White,
    
    surface = Color(0xFF181818),      // Slightly lighter
    onSurface = Color.White,
    
    error = Color(0xFFCF6679),
    onError = Color.Black
)

/**
 * Main theme composable
 * 
 * @param darkTheme: Enable dark theme (default = system setting)
 * @param dynamicColor: Use Material You dynamic colors (Android 12+)
 * @param content: App content
 */
@Composable
fun MusicAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,  // Set false để dùng custom colors
    content: @Composable () -> Unit
) {
    /**
     * Color scheme selection
     */
    val colorScheme = when {
        dynamicColor && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S -> {
            val context = androidx.compose.ui.platform.LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) 
            else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    
    /**
     * Update system bars (status bar, navigation bar)
     */
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            
            // Status bar color
            window.statusBarColor = Color.Transparent.toArgb()
            
            // Navigation bar color
            window.navigationBarColor = Color.Transparent.toArgb()
            
            // Icon colors (dark icons cho light theme)
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }
    
    /**
     * Apply Material3 theme
     */
    MaterialTheme(
        colorScheme = colorScheme,
//        typography = Typography,
        content = content
    )
}
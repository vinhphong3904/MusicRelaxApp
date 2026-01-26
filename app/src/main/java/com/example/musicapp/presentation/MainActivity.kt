package com.example.musicapp.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.musicapp.presentation.auth.AuthUiState
import com.example.musicapp.presentation.auth.AuthViewModel
import com.example.musicapp.presentation.auth.LoginScreen
import com.example.musicapp.presentation.home.HomeScreen
import androidx.navigation.compose.composable
import com.example.musicapp.presentation.theme.MusicAppTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MusicAppTheme {
                val navController = rememberNavController()
                val authViewModel: AuthViewModel = viewModel()
                val uiState by authViewModel.uiState.collectAsState()

                // check 1 lần duy nhất
                LaunchedEffect(Unit) {
                    authViewModel.checkLogin()
                }

                val startDestination = when (uiState) {
                    AuthUiState.LoggedIn -> "home"
                    AuthUiState.Idle,
                    AuthUiState.Loading -> null // chờ
                    else -> "login"
                }

                if (startDestination != null) {
                    NavHost(
                        navController = navController,
                        startDestination = startDestination
                    ) {

                        composable("login") {
                            LoginScreen(navController)
                        }

                        composable("home") {
                            HomeScreen(navController)
                        }
                    }
                }
            }
        }
    }
}

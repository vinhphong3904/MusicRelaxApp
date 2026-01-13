package com.example.musicapp.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.musicapp.presentation.navigation.NavGraph
import com.example.musicapp.presentation.navigation.Screen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            // Đổi startDestination sang Home để bỏ qua màn hình đăng nhập
            NavGraph(navController = navController, startDestination = Screen.Home.route)
        }
    }
}

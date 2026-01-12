package com.example.musicapp.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
//import com.example.musicapp.presentation.library.NavGraph
//import com.example.musicapp.presentation.library.Screen
// Import Theme của bạn (nếu có)
import dagger.hilt.android.AndroidEntryPoint



@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            //NavGraph(navController = navController, startDestination = Screen.Login.route)
        }
    }
}

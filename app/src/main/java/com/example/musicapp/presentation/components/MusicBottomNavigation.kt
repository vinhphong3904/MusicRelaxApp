package com.example.musicapp.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.musicapp.presentation.navigation.Screen

@Composable
fun MusicBottomNavigation(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar(
        containerColor = Color.Black.copy(alpha = 0.95f),
        tonalElevation = 0.dp
    ) {
        val items = listOf(
            NavigationItemData("Home", Icons.Default.Home, Screen.Home.route),
            NavigationItemData("Search", Icons.Default.Search, Screen.Search.route),
            NavigationItemData("Playlist", Icons.Default.AddCircle, Screen.Playlist.route),
            NavigationItemData("Library", Icons.AutoMirrored.Filled.List, Screen.Library.route),
            NavigationItemData("Profile", Icons.Default.Person, Screen.Profile.route)
        )

        items.forEach { item ->
            val isSelected = currentDestination?.hierarchy?.any { it.route == item.route } == true
            
            NavigationBarItem(
                icon = { 
                    Icon(
                        imageVector = item.icon, 
                        contentDescription = item.label,
                        tint = if (isSelected) Color.White else Color.Gray
                    ) 
                },
                label = { 
                    Text(
                        text = item.label, 
                        fontSize = 10.sp,
                        color = if (isSelected) Color.White else Color.Gray,
                        fontWeight = if (isSelected) androidx.compose.ui.text.font.FontWeight.Bold else androidx.compose.ui.text.font.FontWeight.Normal
                    ) 
                },
                selected = isSelected,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    selectedTextColor = Color.White,
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}

data class NavigationItemData(val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector, val route: String)

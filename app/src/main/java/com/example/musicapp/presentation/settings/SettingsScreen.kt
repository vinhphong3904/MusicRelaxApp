package com.example.musicapp.presentation.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavHostController) {
    Scaffold(
        containerColor = Color(0xFF121212),
        topBar = {
            TopAppBar(
                title = { Text("Cài đặt", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF121212))
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            SettingsSection(title = "Tài khoản") {
                SettingsItem(icon = Icons.Default.Person, title = "Thông tin cá nhân")
                SettingsItem(icon = Icons.Default.Lock, title = "Quyền riêng tư")
            }
            
            SettingsSection(title = "Ứng dụng") {
                SettingsItem(icon = Icons.Default.Notifications, title = "Thông báo")
                SettingsItem(icon = Icons.Default.Info, title = "Phiên bản ứng dụng", value = "1.0.0")
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            TextButton(
                onClick = { /* Handle Logout */ },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Đăng xuất", color = Color.Red, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier = Modifier.padding(top = 16.dp)) {
        Text(
            text = title,
            color = Color.Gray,
            fontSize = 14.sp,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        content()
    }
}

@Composable
fun SettingsItem(icon: ImageVector, title: String, value: String? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(title, color = Color.White, fontSize = 16.sp, modifier = Modifier.weight(1f))
        if (value != null) {
            Text(value, color = Color.Gray, fontSize = 14.sp)
        } else {
            Icon(Icons.Default.KeyboardArrowRight, contentDescription = null, tint = Color.Gray)
        }
    }
}

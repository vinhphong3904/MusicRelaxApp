package com.example.musicapp.presentation.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.musicapp.presentation.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavHostController) {
    // Giả lập trạng thái đăng nhập (Trong thực tế nên lấy từ AuthViewModel hoặc UserDataStore)
    var isLoggedIn by remember { mutableStateOf(false) }
    var userName by remember { mutableStateOf("Kha") }
    var userAvatar by remember { mutableStateOf("K") }
    
    var showEditDialog by remember { mutableStateOf(false) }
    var tempName by remember { mutableStateOf(userName) }

    Scaffold(
        containerColor = Color.Black
        // Đã xóa MusicBottomNavigation vì đã được quản lý tập trung ở MainActivity
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // User Profile Header
            item {
                if (!isLoggedIn) {
                    // Trạng thái chưa đăng nhập
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF2A2A2A)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(40.dp))
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Bạn chưa đăng nhập", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        Text("Đăng nhập để trải nghiệm đầy đủ tính năng", color = Color.Gray, fontSize = 14.sp)
                        
                        Spacer(modifier = Modifier.height(32.dp))
                        
                        // Nút Đăng nhập (Màu xanh nổi bật)
                        Button(
                            onClick = { 
                                navController.navigate(Screen.Login.route)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1DB954)),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.fillMaxWidth().height(48.dp)
                        ) {
                            Text("Đăng nhập", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        // Nút Vào Home (Khám phá ngay - Style hiện đại)
                        OutlinedButton(
                            onClick = { 
                                navController.navigate(Screen.Home.route)
                            },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.fillMaxWidth().height(48.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Khám phá ngay", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                        }
                    }
                } else {
                    // Trạng thái đã đăng nhập
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .clickable { showEditDialog = true },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFF8A80)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(userAvatar.take(1), color = Color.Black, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(userName, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            Text("Chạm để chỉnh sửa hồ sơ", color = Color.Gray, fontSize = 12.sp)
                        }
                        Icon(Icons.Default.Edit, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                    }
                }
                HorizontalDivider(color = Color(0xFF1A1A1A), thickness = 0.5.dp)
            }

            // Menu Options
            item {
                ProfileMenuItem("Thêm tài khoản", Icons.Default.Add) {
                    navController.navigate(Screen.Register.route)
                }
                ProfileMenuItem("Có gì mới", Icons.Default.Info) {}
                ProfileMenuItem("Số liệu hoạt động nghe", Icons.Default.List) {}
                ProfileMenuItem("Gần đây", Icons.Default.Refresh) {}
                ProfileMenuItem("Tin cập nhật", Icons.Default.Notifications) {}
                ProfileMenuItem("Cài đặt và quyền riêng tư", Icons.Default.Settings) {}
                
                if (isLoggedIn) {
                    ProfileMenuItem("Đăng xuất", Icons.Default.ExitToApp, Color.Red) {
                        isLoggedIn = false
                    }
                }
            }
        }

        // Dialog chỉnh sửa tên
        if (showEditDialog) {
            AlertDialog(
                onDismissRequest = { showEditDialog = false },
                containerColor = Color(0xFF2A2A2A),
                title = { Text("Chỉnh sửa hồ sơ", color = Color.White) },
                text = {
                    Column {
                        Text("Tên người dùng", color = Color.Gray, fontSize = 12.sp)
                        TextField(
                            value = tempName,
                            onValueChange = { tempName = it },
                            colors = TextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent
                            )
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        userName = tempName
                        userAvatar = if(tempName.isNotEmpty()) tempName.take(1) else "K"
                        showEditDialog = false
                    }) {
                        Text("Lưu", color = Color(0xFF1DB954), fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showEditDialog = false }) {
                        Text("Hủy", color = Color.Gray)
                    }
                }
            )
        }
    }
}

@Composable
fun ProfileMenuItem(
    text: String, 
    icon: androidx.compose.ui.graphics.vector.ImageVector, 
    color: Color = Color.White,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(22.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(text, color = color, fontSize = 15.sp)
    }
}

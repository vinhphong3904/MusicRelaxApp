package com.example.musicapp.presentation.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.musicapp.R
import com.example.musicapp.presentation.navigation.Screen
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(navController: NavHostController) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    
    var userName by remember { mutableStateOf("Kha") }
    var showEditDialog by remember { mutableStateOf(false) }
    val userInitial = if (userName.isNotEmpty()) userName.take(1).uppercase() else "U"

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Color(0xFF121212),
                modifier = Modifier.width(300.dp).fillMaxHeight()
            ) {
                ProfileSidebarContent(userName, userInitial, onEditClick = { showEditDialog = true })
            }
        }
    ) {
        Scaffold(
            containerColor = Color(0xFF121212),
            bottomBar = { MusicBottomNavigation(navController) }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {
                HomeHeader(userInitial, onProfileClick = { scope.launch { drawerState.open() } })

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text(
                            text = "Giai điệu thịnh hành",
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    val mockSongs = listOf(
                        Triple("Đừng Làm Trái Tim Anh Đau", "Sơn Tùng M-TP", R.drawable.icon),
                        Triple("Thiên Lý Ơi", "Jack - J97", R.drawable.nen),
                        Triple("Giá Như", "SOOBIN", R.drawable.tieude),
                        Triple("Exit Sign", "HIEUTHUHAI", R.drawable.icon),
                        Triple("Em Xinh", "MONO", R.drawable.nen)
                    )

                    items(15) { index ->
                        val song = mockSongs[index % mockSongs.size]
                        MusicModernCard(
                            artistName = song.second,
                            songName = song.first,
                            artistImage = song.third,
                            onClick = { 
                                // ĐÃ FIX: Chuyển hướng sang màn hình Player
                                navController.navigate(Screen.Player.route) 
                            }
                        )
                    }
                    
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
    }

    if (showEditDialog) {
        var tempName by remember { mutableStateOf(userName) }
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            containerColor = Color(0xFF2A2A2A),
            title = { Text("Đổi tên hiển thị", color = Color.White) },
            text = {
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
            },
            confirmButton = {
                TextButton(onClick = {
                    userName = tempName
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

@Composable
fun MusicModernCard(artistName: String, songName: String, artistImage: Int, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }, // Đã kích hoạt sự kiện nhấn
        color = Color(0xFFD1D5DB),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = artistImage),
                contentDescription = null,
                modifier = Modifier.size(60.dp).clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = artistName, color = Color(0xFF6B7280), fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Text(text = songName, color = Color.Black, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, maxLines = 1)
            }
            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.DarkGray, modifier = Modifier.size(28.dp))
        }
    }
}

@Composable
fun HomeHeader(userInitial: String, onProfileClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color(0xFFFF8A80))
                .clickable { onProfileClick() },
            contentAlignment = Alignment.Center
        ) {
            Text(userInitial, color = Color.Black, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.width(12.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            item { FilterChipSimple("Tất cả", isSelected = true) }
            item { FilterChipSimple("Âm nhạc", isSelected = false) }
            item { FilterChipSimple("Podcasts", isSelected = false) }
        }
    }
}

@Composable
fun ProfileSidebarContent(userName: String, userInitial: String, onEditClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(vertical = 24.dp).clickable { onEditClick() },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(56.dp).clip(CircleShape).background(Color(0xFFFF8A80)),
                contentAlignment = Alignment.Center
            ) {
                Text(userInitial, color = Color.Black, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(userName, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text("Xem hồ sơ", color = Color.Gray, fontSize = 13.sp)
            }
        }
        Box(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(Color.DarkGray))
        Spacer(modifier = Modifier.height(16.dp))
        SidebarMenuItem(Icons.Default.Add, "Thêm tài khoản")
        SidebarMenuItem(Icons.Default.Info, "Có gì mới")
        SidebarMenuItem(Icons.AutoMirrored.Filled.List, "Số liệu hoạt động nghe")
        SidebarMenuItem(Icons.Default.Refresh, "Gần đây")
        SidebarMenuItem(Icons.Default.Notifications, "Tin cập nhật")
        SidebarMenuItem(Icons.Default.Settings, "Cài đặt và quyền riêng tư")
    }
}

@Composable
fun SidebarMenuItem(icon: ImageVector, text: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(text, color = Color.White, fontSize = 15.sp)
    }
}

@Composable
fun FilterChipSimple(text: String, isSelected: Boolean) {
    Surface(
        color = if (isSelected) Color(0xFF1DB954) else Color(0xFF2A2A2A),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.height(32.dp)
    ) {
        Text(
            text = text,
            color = if (isSelected) Color.Black else Color.White,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun MusicBottomNavigation(navController: NavHostController) {
    val currentRoute = navController.currentBackStackEntry?.destination?.route
    NavigationBar(containerColor = Color.Black.copy(alpha = 0.9f), tonalElevation = 0.dp) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = null) },
            label = { Text("Home", fontSize = 10.sp) },
            selected = currentRoute == Screen.Home.route,
            onClick = { if (currentRoute != Screen.Home.route) navController.navigate(Screen.Home.route) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Search, contentDescription = null) },
            label = { Text("Search", fontSize = 10.sp) },
            selected = currentRoute == Screen.Search.route,
            onClick = { if (currentRoute != Screen.Search.route) navController.navigate(Screen.Search.route) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.AddCircle, contentDescription = null) },
            label = { Text("PlayList", fontSize = 10.sp) },
            selected = currentRoute == Screen.Playlist.route,
            onClick = { if (currentRoute != Screen.Playlist.route) navController.navigate(Screen.Playlist.route) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = null) },
            label = { Text("Library", fontSize = 10.sp) },
            selected = currentRoute == Screen.Library.route,
            onClick = { if (currentRoute != Screen.Library.route) navController.navigate(Screen.Library.route) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = null) },
            label = { Text("Profile", fontSize = 10.sp) },
            selected = currentRoute == Screen.Profile.route,
            onClick = { if (currentRoute != Screen.Profile.route) navController.navigate(Screen.Profile.route) }
        )
    }
}

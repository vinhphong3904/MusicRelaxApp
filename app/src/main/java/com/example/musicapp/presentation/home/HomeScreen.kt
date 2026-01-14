package com.example.musicapp.presentation.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
fun HomeScreen(
    navController: NavHostController,
    onSongSelect: (Triple<String, String, Int>) -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    
    var userName by remember { mutableStateOf("Kha") }
    var showEditDialog by remember { mutableStateOf(false) }
    val userInitial = if (userName.isNotEmpty()) userName.take(1).uppercase() else "U"

    // DANH SÁCH BÀI HÁT MỚI (ĐỒNG BỘ VỚI MAIN ACTIVITY)
    val playlist = listOf(
        Triple("Đừng Làm Trái Tim Anh Đau", "Sơn Tùng M-TP", R.drawable.icon),
        Triple("Chúng Ta Của Tương Lai", "Sơn Tùng M-TP", R.drawable.tieude),
        Triple("Thiên Lý Ơi", "Jack - J97", R.drawable.nen),
        Triple("Giá Như", "SOOBIN", R.drawable.tieude),
        Triple("Exit Sign", "HIEUTHUHAI", R.drawable.icon),
        Triple("Em Xinh", "MONO", R.drawable.nen),
        Triple("Lệ Lưu Ly", "Vũ Phụng Tiên", R.drawable.tieude),
        Triple("Cắt Đôi Nỗi Sầu", "Tăng Duy Tân", R.drawable.icon),
        Triple("Ngày Mai Người Ta Lấy Chồng", "Anh Tú", R.drawable.nen),
        Triple("Mưa Tháng Sáu", "Văn Mai Hương", R.drawable.tieude),
        Triple("Nơi Này Có Anh", "Sơn Tùng M-TP", R.drawable.icon),
        Triple("Lạc Trôi", "Sơn Tùng M-TP", R.drawable.nen),
        Triple("Sau Lời Từ Khước", "Phan Mạnh Quỳnh", R.drawable.tieude),
        Triple("Thanh Xuân", "Da LAB", R.drawable.icon),
        Triple("Thằng Điên", "JustaTee", R.drawable.nen),
        Triple("Anh Nhà Ở Đâu Thế", "AMEE", R.drawable.tieude),
        Triple("Tòng Phu", "Keyo", R.drawable.icon),
        Triple("See Tình", "Hoàng Thùy Linh", R.drawable.nen),
        Triple("Waiting For You", "MONO", R.drawable.tieude),
        Triple("Khuất Lối", "H-Kray", R.drawable.icon)
    )

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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF121212))
        ) {
            HomeHeader(userInitial, onProfileClick = { scope.launch { drawerState.open() } })

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                // Mục: Dành cho bạn (LazyRow)
                item {
                    Text(
                        text = "Dành cho bạn",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(16.dp)
                    )
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(playlist.take(6)) { song ->
                            SongSquareCard(song.first, song.second, song.third) { onSongSelect(song) }
                        }
                    }
                }

                // Mục: Giai điệu thịnh hành (Danh sách dọc)
                item {
                    Text(
                        text = "Giai điệu thịnh hành",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 16.dp, top = 24.dp, bottom = 12.dp)
                    )
                }

                items(playlist.drop(6)) { song ->
                    MusicModernCard(
                        artistName = song.second,
                        songName = song.first,
                        artistImage = song.third,
                        onClick = { onSongSelect(song) }
                    )
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
fun SongSquareCard(title: String, artist: String, imageRes: Int, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .width(140.dp)
            .clickable { onClick() }
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = null,
            modifier = Modifier
                .size(140.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold, maxLines = 1)
        Text(artist, color = Color.Gray, fontSize = 12.sp, maxLines = 1)
    }
}

@Composable
fun MusicModernCard(artistName: String, songName: String, artistImage: Int, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onClick() },
        color = Color(0xFF1E1E1E), // Chỉnh lại màu cho chuyên nghiệp hơn (tối)
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(8.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = artistImage),
                contentDescription = null,
                modifier = Modifier.size(56.dp).clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = songName, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                Text(text = artistName, color = Color.Gray, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            }
            Icon(Icons.Default.MoreVert, contentDescription = null, tint = Color.Gray)
        }
    }
}

@Composable
fun HomeHeader(userInitial: String, onProfileClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(top = 12.dp, start = 16.dp, end = 16.dp, bottom = 8.dp),
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

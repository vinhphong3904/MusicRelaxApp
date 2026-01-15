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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.musicapp.R
import com.example.musicapp.presentation.viewmodel.SongsViewModel
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    navController: NavHostController,
    onSongSelect: (Triple<String, String, Int>) -> Unit,
    viewModel: SongsViewModel = hiltViewModel()
) {

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    var userName by remember { mutableStateOf("Kha") }
    var showEditDialog by remember { mutableStateOf(false) }
    val userInitial = if (userName.isNotEmpty()) userName.take(1).uppercase() else "U"

    // Observe state từ ViewModel
    val recommendSongs by viewModel.recommendSongs.collectAsState()
    val topSongs by viewModel.topSongs.collectAsState()


    // Gọi API khi vào màn hình
    LaunchedEffect(Unit) {
        viewModel.loadRecommendSongs() // token thật từ Auth
        viewModel.loadTopSongs()
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Color(0xFF121212),
                modifier = Modifier.width(280.dp).fillMaxHeight()
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
                // Mục: Dành cho bạn (Recommend)
                item {
                    Text(
                        text = "Dành cho bạn",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                    )
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(recommendSongs) { song ->
                            SongSquareCard(
                                title = song.title,
                                artist = "ID:${song.artist_id}", // hoặc map thêm artistName nếu có
                                imageRes = R.drawable.icon // tạm icon, sau này load ảnh từ cover_image_url
                            ) {
                                onSongSelect(Triple(song.title, "ID:${song.artist_id}", R.drawable.icon))
                            }
                        }
                    }
                }

                // Mục: Giai điệu thịnh hành (Top 10 view)
                item {
                    Text(
                        text = "Giai điệu thịnh hành",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 16.dp, top = 20.dp, bottom = 10.dp)
                    )
                }

                items(topSongs) { song ->
                    MusicModernCard(
                        artistName = "ID:${song.artist_id}", // hoặc map thêm artistName nếu có
                        songName = song.title,
                        artistImage = R.drawable.icon, // tạm icon, sau này load ảnh từ cover_image_url
                        onClick = {
                            onSongSelect(Triple(song.title, "ID:${song.artist_id}", R.drawable.icon))
                        }
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
            .width(120.dp) // Thu nhỏ Card từ 140dp -> 120dp
            .clickable { onClick() }
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = null,
            modifier = Modifier
                .size(120.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold, maxLines = 1)
        Text(artist, color = Color.Gray, fontSize = 11.sp, maxLines = 1)
    }
}
//@Composable
//fun SongSquareCard(
//    title: String,
//    artist: String,
//    imageUrl: String, // truyền tên file hoặc URL
//    onClick: () -> Unit
//) {
//    Column(
//        modifier = Modifier
//            .width(120.dp)
//            .clickable { onClick() }
//    ) {
//        Image(
//            painter = rememberAsyncImagePainter(model = imageUrl),
//            contentDescription = null,
//            modifier = Modifier
//                .size(120.dp)
//                .clip(RoundedCornerShape(8.dp)),
//            contentScale = ContentScale.Crop
//        )
//        Spacer(modifier = Modifier.height(6.dp))
//        Text(
//            text = title,
//            color = Color.White,
//            fontSize = 13.sp,
//            fontWeight = FontWeight.Bold,
//            maxLines = 1
//        )
//        Text(
//            text = artist,
//            color = Color.Gray,
//            fontSize = 11.sp,
//            maxLines = 1
//        )
//    }
//}


@Composable
fun MusicModernCard(artistName: String, songName: String, artistImage: Int, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp) // Thu nhỏ vertical padding
            .clickable { onClick() },
        color = Color(0xFF1E1E1E),
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            modifier = Modifier.padding(6.dp).fillMaxWidth(), // Thu nhỏ padding nội bộ
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = artistImage),
                contentDescription = null,
                modifier = Modifier.size(48.dp).clip(RoundedCornerShape(6.dp)), // Thu nhỏ ảnh 56dp -> 48dp
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = songName, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                Text(text = artistName, color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }
            Icon(Icons.Default.MoreVert, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
fun HomeHeader(userInitial: String, onProfileClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(top = 8.dp, start = 16.dp, end = 16.dp, bottom = 6.dp), // Thu nhỏ padding top 12dp -> 8dp
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(30.dp) // Thu nhỏ avatar 36dp -> 30dp
                .clip(CircleShape)
                .background(Color(0xFFFF8A80))
                .clickable { onProfileClick() },
            contentAlignment = Alignment.Center
        ) {
            Text(userInitial, color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
        Spacer(modifier = Modifier.width(10.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            item { FilterChipSimple("Tất cả", isSelected = true) }
            item { FilterChipSimple("Âm nhạc", isSelected = false) }
            item { FilterChipSimple("Podcasts", isSelected = false) }
        }
    }
}

@Composable
fun ProfileSidebarContent(userName: String, userInitial: String, onEditClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(14.dp)
    ) {
        Row(
            modifier = Modifier.padding(vertical = 20.dp).clickable { onEditClick() },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(48.dp).clip(CircleShape).background(Color(0xFFFF8A80)),
                contentAlignment = Alignment.Center
            ) {
                Text(userInitial, color = Color.Black, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(userName, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text("Xem hồ sơ", color = Color.Gray, fontSize = 12.sp)
            }
        }
        Box(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(Color.DarkGray))
        Spacer(modifier = Modifier.height(14.dp))
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
        modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
        Spacer(modifier = Modifier.width(14.dp))
        Text(text, color = Color.White, fontSize = 14.sp)
    }
}

@Composable
fun FilterChipSimple(text: String, isSelected: Boolean) {
    Surface(
        color = if (isSelected) Color(0xFF1DB954) else Color(0xFF2A2A2A),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.height(28.dp)
    ) {
        Text(
            text = text,
            color = if (isSelected) Color.Black else Color.White,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

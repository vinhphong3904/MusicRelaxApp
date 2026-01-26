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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.musicapp.R
import com.example.musicapp.data.model.UserDto
import com.example.musicapp.presentation.navigation.Screen
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: HomeViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Color(0xFF121212),
                modifier = Modifier
                    .width(280.dp)
                    .fillMaxHeight()
            ) {
                ProfileSidebarContent(
                    user = state.user,
                    userInitial = state.userInitial,
                    onProfileClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(Screen.Profile.route)
                    }
                )
            }
        }
    ) {

        // Loading content và kết nối api
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF121212))
        ) {

            //Nếu api lỗi
            if (state.isApiError) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.Clear,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = state.error?:"Lỗi không thể tải dữ liệu",
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        fontSize = 16.sp
                    )
                }
            }

            //Home Ui
            else {

                Column(
                    modifier = Modifier.fillMaxSize()
                ) {

                    HomeHeader(
                        userInitial = state.userInitial
                    ) {
                        scope.launch { drawerState.open() }
                    }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {

                        // Dành cho bạn
                        item {
                            if (state.recommendSongs.isNotEmpty()) {
                                Text(
                                    text = "Dành cho bạn",
                                    color = Color.White,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(
                                        horizontal = 16.dp,
                                        vertical = 12.dp
                                    )
                                )
                                LazyRow(
                                    contentPadding = PaddingValues(horizontal = 16.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    items(state.recommendSongs) { song ->
                                        SongSquareCard(
                                            title = song.title,
                                            artist = song.artist.name,
                                            imageRes = R.drawable.icon
                                        ) {
                                            navController.navigate(Screen.Player.route)
                                        }
                                    }
                                }
                            }
                        }

                        // Giai điệu thịnh hành
                        item {
                            Text(
                                text = "Giai điệu thịnh hành",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(
                                    start = 16.dp,
                                    top = 20.dp,
                                    bottom = 10.dp
                                )
                            )
                        }

                        items(state.topSongs) { song ->
                            MusicModernCard(
                                artistName = song.artist.name,
                                songName = song.title,
                                artistImage = R.drawable.nen
                            ) {
                                navController.navigate(Screen.Player.route)
                            }
                        }
                    }
                }
            }
        }
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
fun HomeHeader(userInitial: String, onOpenSidebar: () -> Unit) {
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
                .clickable { onOpenSidebar() },
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
fun ProfileSidebarContent(
    user: UserDto?,
    userInitial: String,
    onProfileClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(14.dp)
    ) {

        // AVATAR + NAME
        Row(
            modifier = Modifier
                .padding(vertical = 20.dp)
                .clickable(enabled = user != null) {
                    onProfileClick()
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFF8A80)),
                contentAlignment = Alignment.Center
            ) {
                Text(userInitial, color = Color.Black, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = user?.fullName ?: "Khách",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (user != null) "Xem hồ sơ" else "Đăng nhập để xem hồ sơ",
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }
        }

        Box(
            modifier = Modifier.fillMaxWidth().height(0.5.dp).background(Color.DarkGray)
        )

        Spacer(modifier = Modifier.height(14.dp))

        // CHỈ USER MỚI CÓ
        if (user != null) {
            SidebarMenuItem(Icons.Default.Refresh, "Gần đây")
            SidebarMenuItem(Icons.Default.Settings, "Cài đặt")
        }
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

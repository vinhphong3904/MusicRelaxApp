package com.example.musicapp.presentation.library

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.musicapp.presentation.home.MusicBottomNavigation
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistScreen(navController: NavHostController) {
    var showCreateSheet by remember { mutableStateOf(false) }
    var showNameInputDialog by remember { mutableStateOf(false) }
    var newPlaylistName by remember { mutableStateOf("") }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Danh sách playlist giả lập để hiển thị sau khi tạo
    val playlists = remember { mutableStateListOf<String>() }

    Scaffold(
        containerColor = Color.Black,
        bottomBar = { MusicBottomNavigation(navController) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                PlaylistListHeader()
                
                if (playlists.isEmpty()) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Chưa có danh sách phát nào",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Nhấn vào nút bên dưới để tạo mới",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                } else {
                    // Hiển thị danh sách các playlist đã tạo
                    Column(modifier = Modifier.padding(16.dp)) {
                        playlists.forEach { name ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(50.dp)
                                        .background(Color.DarkGray, RoundedCornerShape(4.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    // Thay MusicNote bằng PlayArrow để fix lỗi Unresolved reference
                                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.Gray)
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(name, color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            FloatingActionButton(
                onClick = { showCreateSheet = true },
                containerColor = Color.White,
                shape = CircleShape,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
                    .size(56.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black)
            }

            if (showCreateSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showCreateSheet = false },
                    sheetState = sheetState,
                    containerColor = Color(0xFF282828),
                    dragHandle = null
                ) {
                    CreatePlaylistSheetContent(
                        onDismiss = { showCreateSheet = false },
                        onCreatePlaylist = {
                            showCreateSheet = false
                            showNameInputDialog = true
                        }
                    )
                }
            }

            if (showNameInputDialog) {
                AlertDialog(
                    onDismissRequest = { showNameInputDialog = false },
                    containerColor = Color(0xFF282828),
                    title = { Text("Đặt tên cho danh sách phát", color = Color.White) },
                    text = {
                        TextField(
                            value = newPlaylistName,
                            onValueChange = { newPlaylistName = it },
                            placeholder = { Text("Tên danh sách phát của tôi #1") },
                            colors = TextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent
                            ),
                            singleLine = true
                        )
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                if (newPlaylistName.isNotBlank()) {
                                    playlists.add(newPlaylistName)
                                    val nameCreated = newPlaylistName
                                    newPlaylistName = ""
                                    showNameInputDialog = false
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Đã tạo thành công: $nameCreated")
                                    }
                                }
                            }
                        ) {
                            Text("Tạo", color = Color(0xFF1DB954), fontWeight = FontWeight.Bold)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showNameInputDialog = false }) {
                            Text("Hủy", color = Color.White)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun PlaylistListHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(Color(0xFFFF8A80)),
            contentAlignment = Alignment.Center
        ) {
            Text("K", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
        Spacer(modifier = Modifier.width(12.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            item { FilterChipItem("Tất cả", isSelected = true) }
            item { FilterChipItem("Âm nhạc", isSelected = false) }
            item { FilterChipItem("Podcasts", isSelected = false) }
            item { FilterChipItem("Wrapped", isSelected = false) }
        }
    }
}

@Composable
fun FilterChipItem(text: String, isSelected: Boolean) {
    Surface(
        color = if (isSelected) Color(0xFF1DB954) else Color(0xFF1A1A1A),
        shape = RoundedCornerShape(20.dp)
    ) {
        Text(
            text = text,
            color = if (isSelected) Color.Black else Color.White,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun CreatePlaylistSheetContent(
    onDismiss: () -> Unit,
    onCreatePlaylist: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        CreateOptionItem(
            icon = Icons.Default.List,
            title = "Danh sách phát",
            description = "Tạo danh sách phát gồm các bài hát hoặc tập podcast",
            onClick = onCreatePlaylist
        )
        Spacer(modifier = Modifier.height(24.dp))
        CreateOptionItem(
            icon = Icons.Default.Person,
            title = "Danh sách phát cộng tác",
            description = "Cùng bạn bè tạo danh sách phát",
            onClick = { /* Handle collab */ }
        )
        Spacer(modifier = Modifier.height(24.dp))
        CreateOptionItem(
            icon = Icons.Default.Favorite,
            title = "Giai điệu chung",
            description = "Tạo danh sách phát tổng hợp gu nhạc của bạn bè",
            onClick = { /* Handle blend */ }
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .size(48.dp)
                    .background(Color.White.copy(alpha = 0.1f), CircleShape)
            ) {
                Icon(Icons.Default.Close, contentDescription = null, tint = Color.White)
            }
        }
    }
}

@Composable
fun CreateOptionItem(
    icon: ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(Color.White.copy(alpha = 0.1f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text(description, color = Color.Gray, fontSize = 13.sp)
        }
    }
}

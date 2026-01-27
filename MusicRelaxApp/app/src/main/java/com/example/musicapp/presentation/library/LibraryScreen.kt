package com.example.musicapp.presentation.library

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.musicapp.data.api.ApiClient
import com.example.musicapp.data.model.PlaylistDto
import com.example.musicapp.data.model.PlaylistRequest
import com.example.musicapp.presentation.navigation.Screen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun LibraryScreen(navController: NavHostController) {
    val scope = rememberCoroutineScope()
    var isSearching by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var showSortSheet by remember { mutableStateOf(false) }
    var selectedSort by remember { mutableStateOf("Gần đây") }
    
    val focusRequester = remember { FocusRequester() }

    var playlists by remember { mutableStateOf<List<PlaylistDto>>(emptyList()) }
    var userName by remember { mutableStateOf("User") }
    var favoriteCount by remember { mutableIntStateOf(0) }
    var historyCount by remember { mutableIntStateOf(0) }
    var isLoading by remember { mutableStateOf(true) }

    var showCreateDialog by remember { mutableStateOf(false) }
    var newPlaylistName by remember { mutableStateOf("") }
    var playlistToDelete by remember { mutableStateOf<PlaylistDto?>(null) }

    val refreshData = {
        scope.launch {
            try {
                val userResponse = ApiClient.musicApi.getMe()
                if (userResponse.success) userName = userResponse.user.username
                
                val playlistResponse = ApiClient.musicApi.getPlaylists()
                if (playlistResponse.success) playlists = playlistResponse.data

                val favResponse = ApiClient.musicApi.getFavorites()
                if (favResponse.success) favoriteCount = favResponse.data.size

                val historyResponse = ApiClient.musicApi.getHistories()
                if (historyResponse.success) historyCount = historyResponse.data.size
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(Unit) { refreshData() }

    // Tự động mở bàn phím khi nhấn tìm kiếm
    LaunchedEffect(isSearching) {
        if (isSearching) {
            focusRequester.requestFocus()
        }
    }

    val sheetState = rememberModalBottomSheetState()
    val filteredPlaylists = remember(searchQuery, playlists) {
        if (searchQuery.isEmpty()) playlists
        else playlists.filter { it.name.contains(searchQuery, ignoreCase = true) }
    }

    Scaffold(
        containerColor = Color.Black
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(bottom = padding.calculateBottomPadding())) {
            // Header Section
            Column(modifier = Modifier.fillMaxWidth().background(Color.Black).statusBarsPadding().padding(top = 8.dp, bottom = 4.dp)) {
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    if (isSearching) {
                        TextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp) // Tăng chiều cao để không bị che text
                                .focusRequester(focusRequester),
                            placeholder = { Text("Tìm trong thư viện", fontSize = 14.sp) },
                            leadingIcon = { Icon(Icons.Default.Search, null, modifier = Modifier.size(20.dp)) },
                            trailingIcon = {
                                IconButton(onClick = { 
                                    isSearching = false
                                    searchQuery = ""
                                }) {
                                    Icon(Icons.Default.Close, null, tint = Color.White)
                                }
                            },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFF282828),
                                unfocusedContainerColor = Color(0xFF282828),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search)
                        )
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(28.dp).clip(CircleShape).background(Color(0xFFFF8A80)), contentAlignment = Alignment.Center) {
                                Text(userName.take(1).uppercase(), color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Thư viện", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        }
                        Row {
                            IconButton(onClick = { isSearching = true }) { Icon(Icons.Default.Search, null, tint = Color.White, modifier = Modifier.size(22.dp)) }
                            IconButton(onClick = { showCreateDialog = true }) { Icon(Icons.Default.Add, null, tint = Color.White, modifier = Modifier.size(24.dp)) }
                        }
                    }
                }
                
                if (!isSearching) {
                    Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        LibraryFilterChip("Danh sách phát")
                        LibraryFilterChip("Nghệ sĩ")
                        LibraryFilterChip("Podcast")
                    }
                }
            }

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = Color(0xFF1DB954)) }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    item {
                        LibraryItem(name = "Bài hát đã thích", type = "Danh sách phát • $favoriteCount bài hát", icon = Icons.Default.Favorite, iconTint = Color(0xFF1DB954), onClick = { navController.navigate(Screen.Favorites.route) })
                    }
                    item {
                        LibraryItem(name = "Lịch sử nghe", type = "Danh sách phát • $historyCount bài hát đã nghe", icon = Icons.Default.Refresh, iconTint = Color.Blue, onClick = { navController.navigate("history") })
                    }
                    items(filteredPlaylists) { playlist -> 
                        LibraryItem(
                            name = playlist.name, 
                            type = "Playlist • ${playlist.song_count} bài hát",
                            icon = Icons.Default.PlayArrow,
                            onLongClick = { playlistToDelete = playlist },
                            onClick = { navController.navigate(Screen.Playlist.route) }
                        ) 
                    }
                }
            }
        }

        // Dialog Tạo Playlist
        if (showCreateDialog) {
            AlertDialog(
                onDismissRequest = { showCreateDialog = false },
                containerColor = Color(0xFF2A2A2A),
                title = { Text("Tạo danh sách phát mới", color = Color.White) },
                text = {
                    TextField(
                        value = newPlaylistName, onValueChange = { newPlaylistName = it },
                        placeholder = { Text("Tên danh sách phát") },
                        colors = TextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent)
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        if (newPlaylistName.isNotBlank()) {
                            scope.launch {
                                val res = ApiClient.musicApi.createPlaylist(PlaylistRequest(newPlaylistName))
                                if (res.success) {
                                    showCreateDialog = false
                                    newPlaylistName = ""
                                    refreshData()
                                }
                            }
                        }
                    }) { Text("Tạo", color = Color(0xFF1DB954), fontWeight = FontWeight.Bold) }
                },
                dismissButton = { TextButton(onClick = { showCreateDialog = false }) { Text("Hủy", color = Color.Gray) } }
            )
        }

        // Dialog Xác nhận Xóa
        playlistToDelete?.let { playlist ->
            AlertDialog(
                onDismissRequest = { playlistToDelete = null },
                containerColor = Color(0xFF2A2A2A),
                title = { Text("Xóa danh sách phát?", color = Color.White) },
                text = { Text("Bạn có chắc chắn muốn xóa '${playlist.name}' không?", color = Color.Gray) },
                confirmButton = {
                    TextButton(onClick = {
                        scope.launch {
                            val res = ApiClient.musicApi.deletePlaylist(playlist.id)
                            if (res.success) {
                                playlistToDelete = null
                                refreshData()
                            }
                        }
                    }) { Text("Xóa", color = Color.Red, fontWeight = FontWeight.Bold) }
                },
                dismissButton = { TextButton(onClick = { playlistToDelete = null }) { Text("Hủy", color = Color.Gray) } }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LibraryItem(
    name: String, 
    type: String, 
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color = Color.Gray,
    onLongClick: (() -> Unit)? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
            .padding(horizontal = 16.dp, vertical = 8.dp), 
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(56.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFF282828)), contentAlignment = Alignment.Center) {
            Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(28.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 1)
            Text(type, color = Color.Gray, fontSize = 12.sp)
        }
    }
}

@Composable
fun LibraryFilterChip(text: String) {
    Surface(color = Color(0xFF2A2A2A), shape = RoundedCornerShape(12.dp)) {
        Text(text, color = Color.White, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), fontSize = 11.sp)
    }
}

package com.example.musicapp.presentation.library

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
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
    
    val focusRequester = remember { FocusRequester() }

    var playlists by remember { mutableStateOf<List<PlaylistDto>>(emptyList()) }
    var userName by remember { mutableStateOf("User") }
    var favoriteCount by remember { mutableIntStateOf(0) }
    var historyCount by remember { mutableIntStateOf(0) }
    var isLoading by remember { mutableStateOf(true) }

    var showCreateDialog by remember { mutableStateOf(false) }
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

    LaunchedEffect(isSearching) {
        if (isSearching) focusRequester.requestFocus()
    }

    Scaffold(
        containerColor = Color.Black
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(bottom = padding.calculateBottomPadding())) {
            // Header
            Column(modifier = Modifier.fillMaxWidth().background(Color.Black).statusBarsPadding().padding(top = 8.dp, bottom = 4.dp)) {
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    if (isSearching) {
                        TextField(
                            value = searchQuery, onValueChange = { searchQuery = it },
                            modifier = Modifier.fillMaxWidth().height(56.dp).focusRequester(focusRequester),
                            placeholder = { Text("Tìm trong thư viện", fontSize = 14.sp) },
                            leadingIcon = { Icon(Icons.Default.Search, null, modifier = Modifier.size(20.dp)) },
                            trailingIcon = { IconButton(onClick = { isSearching = false; searchQuery = "" }) { Icon(Icons.Default.Close, null, tint = Color.White) } },
                            colors = TextFieldDefaults.colors(focusedContainerColor = Color(0xFF282828), unfocusedContainerColor = Color(0xFF282828), focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent),
                            shape = RoundedCornerShape(8.dp), singleLine = true, keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search)
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
            }

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = Color(0xFF1DB954)) }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    item {
                        LibraryItem(name = "Bài hát đã thích", type = "Danh sách phát • $favoriteCount bài hát", icon = Icons.Default.Favorite, iconTint = Color(0xFF1DB954), onClick = { navController.navigate(Screen.Favorites.route) })
                    }
                    item {
                        LibraryItem(name = "Lịch sử nghe", type = "Danh sách phát • $historyCount bài hát", icon = Icons.Default.Refresh, iconTint = Color.Blue, onClick = { navController.navigate("history") })
                    }

                    item {
                        Column(modifier = Modifier.padding(vertical = 16.dp)) {
                            Text(
                                "Danh sách phát của bạn", 
                                color = Color.White, 
                                fontSize = 18.sp, 
                                fontWeight = FontWeight.Bold, 
                                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 12.dp)
                            )
                            
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(playlists) { playlist ->
                                    PlaylistCarouselItem(
                                        playlist = playlist,
                                        onClick = { 
                                            // ĐÃ SỬA: Dùng cú pháp tham số tùy chọn ?để khớp với NavGraph
                                            navController.navigate("playlist?playlistId=${playlist.id}") 
                                        },
                                        onLongClick = { playlistToDelete = playlist }
                                    )
                                }
                                item {
                                    Box(
                                        modifier = Modifier.size(120.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFF1A1A1A)).clickable { showCreateDialog = true },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.Add, null, tint = Color.Gray, modifier = Modifier.size(32.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Dialogs
        if (showCreateDialog) {
            var tempName by remember { mutableStateOf("") }
            AlertDialog(
                onDismissRequest = { showCreateDialog = false },
                containerColor = Color(0xFF2A2A2A),
                title = { Text("Tạo danh sách phát mới", color = Color.White) },
                text = {
                    TextField(value = tempName, onValueChange = { tempName = it }, placeholder = { Text("Tên danh sách phát") },
                        colors = TextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent))
                },
                confirmButton = {
                    TextButton(onClick = {
                        if (tempName.isNotBlank()) {
                            scope.launch {
                                if (ApiClient.musicApi.createPlaylist(PlaylistRequest(tempName)).success) {
                                    showCreateDialog = false
                                    refreshData()
                                }
                            }
                        }
                    }) { Text("Tạo", color = Color(0xFF1DB954), fontWeight = FontWeight.Bold) }
                },
                dismissButton = { TextButton(onClick = { showCreateDialog = false }) { Text("Hủy", color = Color.Gray) } }
            )
        }

        playlistToDelete?.let { playlist ->
            AlertDialog(
                onDismissRequest = { playlistToDelete = null },
                containerColor = Color(0xFF2A2A2A),
                title = { Text("Xóa danh sách phát?", color = Color.White) },
                text = { Text("Xóa '${playlist.name}'?", color = Color.Gray) },
                confirmButton = {
                    TextButton(onClick = {
                        scope.launch {
                            if (ApiClient.musicApi.deletePlaylist(playlist.id).success) {
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
fun PlaylistCarouselItem(playlist: PlaylistDto, onClick: () -> Unit, onLongClick: () -> Unit) {
    Column(
        modifier = Modifier
            .width(120.dp)
            .combinedClickable(onClick = onClick, onLongClick = onLongClick),
        horizontalAlignment = Alignment.Start
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF282828)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.PlayArrow, null, tint = Color.Gray, modifier = Modifier.size(48.dp))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(playlist.name, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text("${playlist.song_count} bài hát", color = Color.Gray, fontSize = 11.sp, maxLines = 1)
    }
}

@Composable
fun LibraryItem(name: String, type: String, icon: androidx.compose.ui.graphics.vector.ImageVector, iconTint: Color = Color.Gray, onClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(horizontal = 16.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(56.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFF282828)), contentAlignment = Alignment.Center) {
            Icon(imageVector = icon, null, tint = iconTint, modifier = Modifier.size(28.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
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

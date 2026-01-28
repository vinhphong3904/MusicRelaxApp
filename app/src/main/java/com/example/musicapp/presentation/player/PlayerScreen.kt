package com.example.musicapp.presentation.player

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.musicapp.R
import com.example.musicapp.data.api.ApiClient
import com.example.musicapp.data.model.PlaylistDto
import com.example.musicapp.data.model.PlaylistRequest
import com.example.musicapp.data.model.SongDto
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(
    navController: NavController,
    currentPlayingSong: SongDto?,
    isPlaying: Boolean,
    progress: Float,
    isFavorite: Boolean,
    onFavoriteToggle: (Boolean) -> Unit,
    onPlayPauseChange: (Boolean) -> Unit,
    onProgressChange: (Float) -> Unit,
    onNextPrev: (Int) -> Unit
) {
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() } // THÊM SNACKBAR
    val sheetState = rememberModalBottomSheetState()
    var showMoreOptions by remember { mutableStateOf(false) }
    var showPlaylistPicker by remember { mutableStateOf(false) }
    var showCreatePlaylistDialog by remember { mutableStateOf(false) }
    
    val playlists = remember { mutableStateListOf<PlaylistDto>() }

    LaunchedEffect(showPlaylistPicker) {
        if (showPlaylistPicker) {
            try {
                val res = ApiClient.musicApi.getPlaylists()
                if (res.success) {
                    playlists.clear()
                    playlists.addAll(res.data)
                }
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    if (currentPlayingSong == null) {
        LaunchedEffect(Unit) { navController.popBackStack() }
        return
    }

    val pagerState = rememberPagerState(pageCount = { 2 })
    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(Color(0xFF5D2525), Color(0xFF121212))
    )

    Scaffold(
        containerColor = Color.Transparent,
        modifier = Modifier.background(backgroundBrush),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }, // THÊM HOST SNACKBAR
        topBar = {
            Box(modifier = Modifier.fillMaxWidth().statusBarsPadding().height(64.dp)) {
                Box(
                    modifier = Modifier.align(Alignment.CenterStart).fillMaxHeight().width(72.dp)
                        .pointerInput(Unit) {
                            detectTapGestures(onTap = {
                                focusManager.clearFocus()
                                navController.popBackStack()
                            })
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.KeyboardArrowDown, "Back", tint = Color.White, modifier = Modifier.size(40.dp))
                }

                Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = if (pagerState.currentPage == 0) "ĐANG PHÁT" else "LỜI BÀI HÁT", color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text(currentPlayingSong.title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { padding ->
        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize().padding(padding)) { page ->
            if (page == 0) {
                MainPlayerPage(
                    songTitle = currentPlayingSong.title,
                    artistName = currentPlayingSong.artist_name ?: "Nghệ sĩ #${currentPlayingSong.artist_id}", // DÙNG TÊN NGHỆ SĨ
                    imageRes = R.drawable.icon,
                    isPlaying = isPlaying,
                    progress = progress,
                    isFavorite = isFavorite,
                    onFavoriteToggle = onFavoriteToggle,
                    onPlayPauseClick = { onPlayPauseChange(!isPlaying) },
                    onPrevClick = { onNextPrev(-1) },
                    onNextClick = { onNextPrev(1) },
                    onProgressChange = onProgressChange,
                    onMoreClick = { showMoreOptions = true }
                )
            } else {
                LyricsPage(currentPlayingSong.title)
            }
        }

        if (showMoreOptions) {
            ModalBottomSheet(
                onDismissRequest = { showMoreOptions = false },
                sheetState = sheetState,
                containerColor = Color(0xFF282828)
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)) {
                    ListItem(
                        headlineContent = { Text(if (isFavorite) "Xóa khỏi mục yêu thích" else "Thêm vào yêu thích", color = Color.White) },
                        leadingContent = { Icon(if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder, null, tint = if (isFavorite) Color(0xFF1DB954) else Color.White) },
                        modifier = Modifier.clickable { 
                            onFavoriteToggle(!isFavorite)
                            showMoreOptions = false
                        },
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                    )
                    
                    ListItem(
                        headlineContent = { Text("Thêm vào danh sách phát", color = Color.White) },
                        leadingContent = { Icon(Icons.Default.Add, null, tint = Color.White) },
                        modifier = Modifier.clickable { 
                            showMoreOptions = false
                            showPlaylistPicker = true 
                        },
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                    )
                }
            }
        }

        if (showPlaylistPicker) {
            ModalBottomSheet(
                onDismissRequest = { showPlaylistPicker = false },
                containerColor = Color(0xFF282828)
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)) {
                    Text("Thêm vào danh sách phát", color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.padding(16.dp))
                    
                    ListItem(
                        headlineContent = { Text("Tạo danh sách phát mới", color = Color(0xFF1DB954)) },
                        leadingContent = { Icon(Icons.Default.AddCircle, null, tint = Color(0xFF1DB954)) },
                        modifier = Modifier.clickable { showCreatePlaylistDialog = true },
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                    )
                    
                    playlists.forEach { playlist ->
                        ListItem(
                            headlineContent = { Text(playlist.name, color = Color.White) },
                            leadingContent = { Icon(Icons.Default.List, null, tint = Color.Gray) },
                            modifier = Modifier.clickable { 
                                scope.launch {
                                    try {
                                        val response = ApiClient.musicApi.addSongToPlaylist(playlist.id, mapOf("songId" to currentPlayingSong.id))
                                        showPlaylistPicker = false
                                        snackbarHostState.showSnackbar("Đã thêm vào ${playlist.name}") // HIỆN THÔNG BÁO
                                    } catch (e: Exception) { 
                                        e.printStackTrace() 
                                        snackbarHostState.showSnackbar("Lỗi khi thêm bài hát")
                                    }
                                }
                            },
                            colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                        )
                    }
                }
            }
        }

        if (showCreatePlaylistDialog) {
            var newName by remember { mutableStateOf("") }
            AlertDialog(
                onDismissRequest = { showCreatePlaylistDialog = false },
                containerColor = Color(0xFF2A2A2A),
                title = { Text("Tên danh sách phát", color = Color.White) },
                text = {
                    TextField(value = newName, onValueChange = { newName = it }, placeholder = { Text("Nhập tên...") })
                },
                confirmButton = {
                    TextButton(onClick = {
                        if (newName.isNotBlank()) {
                            scope.launch {
                                try {
                                    val res = ApiClient.musicApi.createPlaylist(PlaylistRequest(newName))
                                    if (res.success) {
                                        ApiClient.musicApi.addSongToPlaylist(res.data.id, mapOf("songId" to currentPlayingSong.id))
                                        showCreatePlaylistDialog = false
                                        showPlaylistPicker = false
                                        snackbarHostState.showSnackbar("Đã tạo và thêm vào $newName") // HIỆN THÔNG BÁO
                                    }
                                } catch (e: Exception) { 
                                    e.printStackTrace() 
                                    snackbarHostState.showSnackbar("Lỗi khi tạo danh sách phát")
                                }
                            }
                        }
                    }) { Text("Tạo & Thêm", color = Color(0xFF1DB954), fontWeight = FontWeight.Bold) }
                }
            )
        }
    }
}

@Composable
fun MainPlayerPage(
    songTitle: String,
    artistName: String,
    imageRes: Int,
    isPlaying: Boolean,
    progress: Float,
    isFavorite: Boolean,
    onFavoriteToggle: (Boolean) -> Unit,
    onPlayPauseClick: () -> Unit,
    onPrevClick: () -> Unit,
    onNextClick: () -> Unit,
    onProgressChange: (Float) -> Unit,
    onMoreClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "Disk Rotation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(animation = tween(15000, easing = LinearEasing)),
        label = "Disk Rotation"
    )

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp).padding(bottom = 16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(modifier = Modifier.weight(1f).fillMaxWidth().padding(vertical = 16.dp), contentAlignment = Alignment.Center) {
            Box(modifier = Modifier.fillMaxHeight(0.85f).aspectRatio(1f).clip(CircleShape).background(Color.Black), contentAlignment = Alignment.Center) {
                Image(painter = painterResource(id = imageRes), contentDescription = null, modifier = Modifier.fillMaxSize().rotate(if (isPlaying) rotation else 0f), contentScale = ContentScale.Crop)
                Box(modifier = Modifier.fillMaxSize(0.2f).clip(CircleShape).background(Color(0xFF121212)))
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(songTitle, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, maxLines = 1)
                Text(artistName, color = Color.Gray, fontSize = 14.sp)
            }
            
            Row {
                IconButton(onClick = { onFavoriteToggle(!isFavorite) }) {
                    Icon(imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder, null, tint = if (isFavorite) Color(0xFF1DB954) else Color.White, modifier = Modifier.size(28.dp))
                }
                
                IconButton(onClick = onMoreClick) {
                    Icon(Icons.Default.Menu, null, tint = Color.White, modifier = Modifier.size(28.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Slider(value = progress, onValueChange = onProgressChange, modifier = Modifier.height(32.dp),
            colors = SliderDefaults.colors(thumbColor = Color.White, activeTrackColor = Color.White, inactiveTrackColor = Color.White.copy(alpha = 0.2f)))
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            val totalSeconds = 250
            val currentSeconds = (progress * totalSeconds).toInt()
            Text(formatTime(currentSeconds), color = Color.Gray, fontSize = 11.sp)
            Text(formatTime(totalSeconds), color = Color.Gray, fontSize = 11.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Refresh, null, tint = Color(0xFF1DB954), modifier = Modifier.size(22.dp))
            IconButton(onClick = onPrevClick) {
                Icon(painterResource(id = android.R.drawable.ic_media_previous), null, tint = Color.White, modifier = Modifier.size(36.dp))
            }
            Surface(modifier = Modifier.size(64.dp).clip(CircleShape).clickable { onPlayPauseClick() }, color = Color.White) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(painterResource(id = if (isPlaying) android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play), null, tint = Color.Black, modifier = Modifier.size(40.dp))
                }
            }
            IconButton(onClick = onNextClick) {
                Icon(painterResource(id = android.R.drawable.ic_media_next), null, tint = Color.White, modifier = Modifier.size(36.dp))
            }
            Icon(Icons.Default.Share, null, tint = Color.White, modifier = Modifier.size(22.dp))
        }
    }
}

fun formatTime(seconds: Int): String {
    val m = seconds / 60
    val s = seconds % 60
    return String.format(Locale.getDefault(), "%d:%02d", m, s)
}

@Composable
fun LyricsPage(title: String) {
    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp).verticalScroll(rememberScrollState())) {
        Spacer(modifier = Modifier.height(16.dp))
        Text("LỜI BÀI HÁT: $title", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, lineHeight = 28.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "LỜI BÀI HÁT ĐANG ĐƯỢC CẬP NHẬT...", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(modifier = Modifier.height(80.dp))
    }
}

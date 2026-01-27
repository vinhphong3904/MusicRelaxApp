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
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(
    navController: NavController,
    currentPlayingSong: Triple<String, String, Int>?,
    isPlaying: Boolean,
    progress: Float,
    onPlayPauseChange: (Boolean) -> Unit,
    onProgressChange: (Float) -> Unit,
    onNextPrev: (Int) -> Unit
) {
    val focusManager = LocalFocusManager.current
    
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
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .height(64.dp)
            ) {
                // NÚT BACK - Sử dụng pointerInput để bắt sự kiện click ngay lập tức
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .fillMaxHeight()
                        .width(72.dp)
                        .pointerInput(Unit) {
                            detectTapGestures(onTap = {
                                focusManager.clearFocus()
                                navController.popBackStack()
                            })
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown, 
                        contentDescription = "Back", 
                        tint = Color.White, 
                        modifier = Modifier.size(40.dp)
                    )
                }

                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = if (pagerState.currentPage == 0) "ĐANG PHÁT" else "LỜI BÀI HÁT", color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text(currentPlayingSong.first, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }

                IconButton(
                    onClick = { },
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    Icon(Icons.Default.MoreVert, null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }
        }
    ) { padding ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize().padding(padding)
        ) { page ->
            if (page == 0) {
                MainPlayerPage(
                    songTitle = currentPlayingSong.first,
                    artistName = currentPlayingSong.second,
                    imageRes = currentPlayingSong.third,
                    isPlaying = isPlaying,
                    progress = progress,
                    onPlayPauseClick = { onPlayPauseChange(!isPlaying) },
                    onPrevClick = { onNextPrev(-1) },
                    onNextClick = { onNextPrev(1) },
                    onProgressChange = onProgressChange
                )
            } else {
                LyricsPage(currentPlayingSong.first)
            }
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
    onPlayPauseClick: () -> Unit,
    onPrevClick: () -> Unit,
    onNextClick: () -> Unit,
    onProgressChange: (Float) -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "Disk Rotation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 15000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Disk Rotation"
    )

    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp).padding(bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight(0.85f)
                    .aspectRatio(1f)
                    .clip(CircleShape)
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .rotate(if (isPlaying) rotation else 0f),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize(0.2f)
                        .clip(CircleShape)
                        .background(Color(0xFF121212))
                )
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(songTitle, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, maxLines = 1)
                Text(artistName, color = Color.Gray, fontSize = 14.sp)
            }
            Icon(Icons.Default.FavoriteBorder, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Slider(
            value = progress,
            onValueChange = onProgressChange,
            modifier = Modifier.height(32.dp),
            colors = SliderDefaults.colors(
                thumbColor = Color.White, 
                activeTrackColor = Color.White, 
                inactiveTrackColor = Color.White.copy(alpha = 0.2f)
            )
        )
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            val totalSeconds = 250
            val currentSeconds = (progress * totalSeconds).toInt()
            Text(formatTime(currentSeconds), color = Color.Gray, fontSize = 11.sp)
            Text(formatTime(totalSeconds), color = Color.Gray, fontSize = 11.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Refresh, contentDescription = null, tint = Color(0xFF1DB954), modifier = Modifier.size(22.dp))
            IconButton(onClick = onPrevClick) {
                Icon(painterResource(id = android.R.drawable.ic_media_previous), null, tint = Color.White, modifier = Modifier.size(36.dp))
            }
            Surface(
                modifier = Modifier.size(64.dp).clip(CircleShape).clickable { onPlayPauseClick() },
                color = Color.White,
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(painterResource(id = if (isPlaying) android.R.drawable.ic_media_pause else android.R.drawable.ic_media_play), null, tint = Color.Black, modifier = Modifier.size(40.dp))
                }
            }
            IconButton(onClick = onNextClick) {
                Icon(painterResource(id = android.R.drawable.ic_media_next), null, tint = Color.White, modifier = Modifier.size(36.dp))
            }
            Icon(Icons.Default.Menu, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
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

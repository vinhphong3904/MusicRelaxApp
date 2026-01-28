package com.example.musicapp.data.service

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.musicapp.data.api.ApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

object GlobalAppState {
    // Danh sách ID bài hát đã thích duy nhất cho toàn ứng dụng
    val favoriteSongIds = mutableStateListOf<Int>()
    
    // Cờ hiệu báo thay đổi cho bài hát yêu thích
    var favoritesChangedFlag by mutableIntStateOf(0)

    // Cờ hiệu báo thay đổi cho danh sách phát (Playlist)
    var playlistsChangedFlag by mutableIntStateOf(0)

    fun updateFavorites(ids: List<Int>) {
        favoriteSongIds.clear()
        favoriteSongIds.addAll(ids.distinct())
    }

    fun isFavorite(songId: Int): Boolean = favoriteSongIds.contains(songId)

    // Hàm thông báo có thay đổi (Dùng sau khi Thêm/Xóa thành công)
    fun notifyChanges() {
        favoritesChangedFlag++
    }

    // Thông báo danh sách phát có thay đổi
    fun notifyPlaylistsChanged() {
        playlistsChangedFlag++
    }

    // Cập nhật cục bộ và thông báo thay đổi ngay lập tức (Optimistic UI)
    fun toggleLocal(songId: Int, shouldFavorite: Boolean) {
        if (shouldFavorite) {
            if (!favoriteSongIds.contains(songId)) favoriteSongIds.add(songId)
        } else {
            favoriteSongIds.removeAll { it == songId }
        }
        notifyChanges()
    }

    // HÀM DÙNG CHUNG: Xóa playlist
    fun deletePlaylistRemote(scope: CoroutineScope, playlistId: Int, onSuccess: () -> Unit) {
        scope.launch {
            try {
                val response = ApiClient.musicApi.deletePlaylist(playlistId)
                if (response.success) {
                    notifyPlaylistsChanged()
                    onSuccess()
                    Log.d("GlobalAppState", "Đã xóa playlist $playlistId thành công")
                }
            } catch (e: Exception) {
                Log.e("GlobalAppState", "Lỗi xóa playlist: ${e.message}")
            }
        }
    }
}

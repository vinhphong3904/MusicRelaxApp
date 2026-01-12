package com.example.musicapp.core.common

/**
 * State cho UI (thay thế Resource cho StateFlow/Compose)
 * Tương tự Resource nhưng tối ưu cho Compose
 */
sealed class UiState<out T> {
    /**
     * Trạng thái ban đầu/đang load
     * UI sẽ hiện Loading indicator
     */
    object Loading : UiState<Nothing>()
    
    /**
     * Có dữ liệu để hiển thị
     * @param data: Dữ liệu cần render (songs, user info...)
     */
    data class Success<T>(val data: T) : UiState<T>()
    
    /**
     * Có lỗi xảy ra
     * @param message: Hiển thị error message cho user
     */
    data class Error(val message: String) : UiState<Nothing>()
}
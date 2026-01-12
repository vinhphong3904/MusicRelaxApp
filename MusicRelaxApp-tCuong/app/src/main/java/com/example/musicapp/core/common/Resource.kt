package com.example.musicapp.core.common

/**
 * Wrapper class cho API response
 * Giúp handle 3 trạng thái: Success, Error, Loading
 * 
 * Generic type T: kiểu dữ liệu trả về (VD: List<Song>, User...)
 */
sealed class Resource<T>(
    val data: T? = null,      // Dữ liệu trả về (null nếu error/loading)
    val message: String? = null // Error message (null nếu success)
) {
    /**
     * Trạng thái thành công
     * VD: Resource.Success(data = listOf(song1, song2))
     */
    class Success<T>(data: T) : Resource<T>(data)
    
    /**
     * Trạng thái lỗi
     * VD: Resource.Error(message = "Network error")
     * 
     * @param message: Mô tả lỗi để hiển thị cho user
     * @param data: Data cũ (nếu có) để hiện tạm khi error
     */
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
    
    /**
     * Trạng thái đang load
     * Dùng để hiện ProgressBar/Shimmer
     */
    class Loading<T>(data: T? = null) : Resource<T>(data)
}
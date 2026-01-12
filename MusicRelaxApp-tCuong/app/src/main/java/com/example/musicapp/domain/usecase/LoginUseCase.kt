package com.example.musicapp.domain.usecase

import com.example.musicapp.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    // Toán tử invoke giúp gọi class như 1 hàm: loginUseCase(e, p)
    suspend operator fun invoke(email: String, pass: String) = repository.login(email, pass)
}
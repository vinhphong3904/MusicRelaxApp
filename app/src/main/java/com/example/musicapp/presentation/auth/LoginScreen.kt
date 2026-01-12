package com.example.musicapp.presentation.auth

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.musicapp.core.common.Resource
import com.example.musicapp.core.common.UiState

/**
 * Màn hình Login
 * 
 * Compose UI:
 * - TextField cho email/password
 * - Button login
 * - Text link "Chưa có tài khoản?"
 * - Hiện loading/error state
 * 
 * @param onLoginSuccess: Callback khi login thành công → navigate to Home
 * @param onNavigateToRegister: Callback navigate to Register screen
 * @param viewModel: Hilt tự inject
 */
@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    // Lắng nghe sự kiện Login thành công hay thất bại
    LaunchedEffect(Unit) {
        viewModel.loginState.collect { result ->
            when (result) {
                is Resource.Success -> {
                    // Chuyển sang màn hình Home và xóa BackStack login
                    navController.navigate("home_screen") {
                        popUpTo("login_screen") { inclusive = true }
                    }
                }
                is Resource.Error -> {
                    Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
                }
                is Resource.Loading -> {
                    // Có thể show dialog loading ở đây
                }
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = viewModel.email,
            onValueChange = { viewModel.email = it },
            label = { Text("Email") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = viewModel.password,
            onValueChange = { viewModel.password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { viewModel.login() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Đăng nhập")
        }
    }
}
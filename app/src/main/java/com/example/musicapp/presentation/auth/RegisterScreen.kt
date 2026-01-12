package com.example.musicapp.presentation.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.musicapp.core.common.UiState

/**
 * Màn hình Register
 *
 * Compose UI:
 * - TextField: Username, Email, Password, Confirm Password
 * - Button đăng ký
 * - Hiển thị loading / error
 *
 * @param onRegisterSuccess: Callback khi đăng ký thành công → quay về Login hoặc Home
 * @param onBackToLogin: Callback quay về Login
 * @param viewModel: Hilt auto inject
 */
//@Composable
//fun RegisterScreen(
//    onRegisterSuccess: () -> Unit,
//    onBackToLogin: () -> Unit,
//    viewModel: AuthViewModel = hiltViewModel()
//) {
//    /**
//     * Collect register state
//     */
//    val registerState by viewModel.registerState.collectAsStateWithLifecycle()
//
//    /**
//     * Local state cho input
//     */
//    var userName by remember { mutableStateOf("") }
//    var email by remember { mutableStateOf("") }
//    var password by remember { mutableStateOf("") }
//    var confirmPassword by remember { mutableStateOf("") }
//
//    /**
//     * Navigate khi register success
//     */
//    LaunchedEffect(registerState) {
//        if (registerState is UiState.Success) {
//            onRegisterSuccess()
//        }
//    }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp),
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center
//    ) {
//
//        /**
//         * Title
//         */
//        Text(
//            text = "Đăng ký",
//            style = MaterialTheme.typography.headlineMedium
//        )
//
//        Spacer(modifier = Modifier.height(32.dp))
//
//        /**
//         * Username
//         */
//        OutlinedTextField(
//            value = userName,
//            onValueChange = { userName = it },
//            label = { Text("Tên người dùng") },
//            modifier = Modifier.fillMaxWidth(),
//            singleLine = true
//        )
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        /**
//         * Email
//         */
//        OutlinedTextField(
//            value = email,
//            onValueChange = { email = it },
//            label = { Text("Email") },
//            modifier = Modifier.fillMaxWidth(),
//            singleLine = true
//        )
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        /**
//         * Password
//         */
//        OutlinedTextField(
//            value = password,
//            onValueChange = { password = it },
//            label = { Text("Password") },
//            modifier = Modifier.fillMaxWidth(),
//            singleLine = true,
//            visualTransformation = PasswordVisualTransformation()
//        )
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        /**
//         * Confirm Password
//         */
//        OutlinedTextField(
//            value = confirmPassword,
//            onValueChange = { confirmPassword = it },
//            label = { Text("Nhập lại password") },
//            modifier = Modifier.fillMaxWidth(),
//            singleLine = true,
//            visualTransformation = PasswordVisualTransformation()
//        )
//
//        Spacer(modifier = Modifier.height(24.dp))
//
//        /**
//         * Register Button
//         */
//        Button(
//            onClick = {
//                viewModel.register(
//                    userName = userName,
//                    email = email,
//                    password = password,
//                    confirmPassword = confirmPassword
//                )
//            },
//            modifier = Modifier.fillMaxWidth(),
//            enabled = registerState !is UiState.Loading
//        ) {
//            if (registerState is UiState.Loading) {
//                CircularProgressIndicator(
//                    modifier = Modifier.size(20.dp),
//                    color = MaterialTheme.colorScheme.onPrimary
//                )
//            } else {
//                Text("Đăng ký")
//            }
//        }
//
//        /**
//         * Error message
//         */
//        if (registerState is UiState.Error) {
//            Spacer(modifier = Modifier.height(16.dp))
//            Text(
//                text = (registerState as UiState.Error).message,
//                color = MaterialTheme.colorScheme.error
//            )
//        }
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        /**
//         * Back to Login
//         */
//        TextButton(onClick = onBackToLogin) {
//            Text("Đã có tài khoản? Đăng nhập")
//        }
//    }
//}

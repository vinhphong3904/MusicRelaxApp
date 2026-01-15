package com.example.musicapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.example.musicapp.presentation.MainActivity
import com.example.musicapp.presentation.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private val authViewModel: AuthViewModel by viewModels()
    private var isLoggedIn: Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val tvPercentage = findViewById<TextView>(R.id.tvPercentage)

        // 1️⃣ check login
        authViewModel.checkLogin()

        lifecycleScope.launch {
            authViewModel.isLoggedIn.collect {
                isLoggedIn = it
            }
        }

        val handler = Handler(Looper.getMainLooper())
        var progress = 0

        Thread {
            while (progress < 100) {
                progress++
                Thread.sleep(25)

                handler.post {
                    progressBar.progress = progress
                    tvPercentage.text = "$progress%"
                }
            }

            handler.post { navigate() }
        }.start()
    }

    private fun navigate() {
        val intent = Intent(
            this,
            MainActivity::class.java
        ).apply {
            putExtra("isLoggedIn", isLoggedIn == true)
        }

        startActivity(intent)
        finish()
    }
}

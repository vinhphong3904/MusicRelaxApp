package com.example.musicapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.musicapp.presentation.MainActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // 1. Khởi tạo SplashScreen của hệ thống (phải gọi trước super.onCreate)
        val splashScreen = installSplashScreen()
        
        super.onCreate(savedInstanceState)
        
        // 2. Thiết lập layout chứa thanh loading và 3 ảnh của bạn
        setContentView(R.layout.activity_splash)

        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val tvPercentage = findViewById<TextView>(R.id.tvPercentage)

        val handler = Handler(Looper.getMainLooper())
        var progressStatus = 0

        // 3. Chạy thanh loading trong 3 giây để chuyển màn hình
        Thread {
            while (progressStatus < 100) {
                progressStatus += 1
                try {
                    Thread.sleep(30) // 30ms * 100 = 3 giây
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

                handler.post {
                    progressBar.progress = progressStatus
                    tvPercentage.text = "$progressStatus%"
                }
            }

            handler.post {
                // Chuyển sang MainActivity (nơi chứa HomeScreen của Compose)
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }.start()
    }
}

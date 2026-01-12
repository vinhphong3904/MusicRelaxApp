package com.example.musicapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.musicapp.presentation.MainActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val tvPercentage = findViewById<TextView>(R.id.tvPercentage)

        val handler = Handler(Looper.getMainLooper())
        var progressStatus = 0

        // Chạy thanh loading trong 5 giây (50ms * 100) để thấy rõ hơn
        Thread {
            while (progressStatus < 100) {
                progressStatus += 1
                try {
                    // 50ms mỗi 1%, tổng cộng là 5 giây
                    Thread.sleep(50)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }

                handler.post {
                    progressBar.progress = progressStatus
                    tvPercentage.text = "$progressStatus%"
                }
            }

            handler.post {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }.start()
    }
}

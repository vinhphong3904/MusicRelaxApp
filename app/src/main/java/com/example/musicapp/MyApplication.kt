package com.example.musicapp
import android.app.Application
import com.example.musicapp.data.service.TokenManager
import dagger.hilt.android.HiltAndroidApp

class MyApplication : Application(){
    override fun onCreate() {
        super.onCreate()
        TokenManager.init(this)
    }
}

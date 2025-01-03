package com.hakutogames.galaxycross

import android.app.Application
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Firebase の初期化
        FirebaseApp.initializeApp(this)
    }
}

package com.example.yansfood

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.yansfood.ui.auth.LoginActivity

/**
 * Forwarder activity yang meneruskan pengguna ke alur autentikasi resmi di package ui.auth.
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
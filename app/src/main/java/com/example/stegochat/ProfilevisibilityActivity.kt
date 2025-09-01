package com.example.stegochat

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ProfilevisibilityActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profilevisibility)

        // Initialize views
        val backButton = findViewById<ImageView>(R.id.backButton)

        // Back Button Click
        backButton?.setOnClickListener {
            finish() // Go back to previous activity
        }
    }
}
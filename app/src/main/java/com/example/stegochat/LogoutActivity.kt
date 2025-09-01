package com.example.stegochat

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LogoutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.logout)

        // Initialize views
        val backButton = findViewById<ImageView>(R.id.backButton)
        val logoutButton = findViewById<Button>(R.id.logoutButton)

        // Back Button Click
        backButton?.setOnClickListener {
            finish() // Go back to previous activity
        }

        // Logout Button Click
        logoutButton?.setOnClickListener {
            Toast.makeText(this, "Logged out successfully!", Toast.LENGTH_SHORT).show()
            // Navigate back to login page
            val intent = Intent(this, Loginpage::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}
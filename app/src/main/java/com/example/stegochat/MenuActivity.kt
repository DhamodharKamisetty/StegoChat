package com.example.stegochat

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast

class MenuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.menu)

        // Initialize menu item views
        val profileItem = findViewById<LinearLayout>(R.id.profileItem)
        val friendRequestsItem = findViewById<LinearLayout>(R.id.friendRequestsItem)
        val hideMessageItem = findViewById<LinearLayout>(R.id.hideMessageItem)
        val revealMessageItem = findViewById<LinearLayout>(R.id.revealMessageItem)
        val settingsItem = findViewById<LinearLayout>(R.id.settingsItem)
        val logoutItem = findViewById<LinearLayout>(R.id.logoutItem)

        // Profile Item Click
        profileItem?.setOnClickListener {
            try {
                Toast.makeText(this, "Navigating to Profile...", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }
        }

        // Friend Requests Item Click
        friendRequestsItem?.setOnClickListener {
            val intent = Intent(this, FriendrequestacceptActivity::class.java)
            startActivity(intent)
        }

        // Hide Message Item Click
        hideMessageItem?.setOnClickListener {
            val intent = Intent(this, HidemessageActivity::class.java)
            startActivity(intent)
        }

        // Reveal Message Item Click
        revealMessageItem?.setOnClickListener {
            val intent = Intent(this, DecryptmessageActivity::class.java)
            startActivity(intent)
        }

        // Settings Item Click
        settingsItem?.setOnClickListener {
            val intent = Intent(this, SoundandvibrationActivity::class.java)
            startActivity(intent)
        }

        // Logout Item Click
        logoutItem?.setOnClickListener {
            val intent = Intent(this, LogoutActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
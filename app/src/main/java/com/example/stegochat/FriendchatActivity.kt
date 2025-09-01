package com.example.stegochat

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class FriendchatActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.friendchat)

        // Handle friend acceptance and invite link onboarding (demo local persistence)
        val prefs = getSharedPreferences("settings", MODE_PRIVATE)
        val friendName = intent.getStringExtra("friend_name")
        val friendStatus = intent.getStringExtra("friend_status")
        val inviteCode = intent.getStringExtra("invite_code")
        if (friendName != null && friendStatus == "accepted") {
            Toast.makeText(this, "Welcome $friendName to your friends list!", Toast.LENGTH_LONG).show()
        }
        // If new user arrived via invite code, mark inviter as a friend locally
        inviteCode?.let { code ->
            val myInviteCode = prefs.getString("invite_code", null)
            if (myInviteCode != null && myInviteCode != code) {
                // In a real backend, we would look up the inviter by code and create mutual friendship
                Toast.makeText(this, "Connected with your inviter!", Toast.LENGTH_SHORT).show()
            }
        }

        // Initialize views
        val menuButton = findViewById<ImageView>(R.id.menuButton)
        val searchButton = findViewById<ImageView>(R.id.searchButton)
        val addFriendButton = findViewById<Button>(R.id.addFriendButton)
        
        // Navigation tabs
        val friendsTab = findViewById<LinearLayout>(R.id.friendsTab)
        val hideMessageTab = findViewById<LinearLayout>(R.id.hideMessageTab)
        val revealMessageTab = findViewById<LinearLayout>(R.id.revealMessageTab)
        val profileTab = findViewById<LinearLayout>(R.id.profileTab)

        // Menu button
        menuButton?.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
        }

        // Search button
        searchButton?.setOnClickListener {
            Toast.makeText(this, "Search functionality coming soon", Toast.LENGTH_SHORT).show()
        }

        // Add friend button
        addFriendButton?.setOnClickListener {
            val intent = Intent(this, FreindrequestActivity::class.java)
            startActivity(intent)
        }

        // Set up friend click listeners
        setupFriendClickListeners()

        // Set up tab navigation
        setupTabNavigation(friendsTab, hideMessageTab, revealMessageTab, profileTab)
    }

    private fun setupFriendClickListeners() {
        val friends = listOf(
            findViewById<LinearLayout>(R.id.friendSarah),
            findViewById<LinearLayout>(R.id.friendMichael),
            findViewById<LinearLayout>(R.id.friendEmma),
            findViewById<LinearLayout>(R.id.friendJames),
            findViewById<LinearLayout>(R.id.friendLisa),
            findViewById<LinearLayout>(R.id.friendDavid),
            findViewById<LinearLayout>(R.id.friendAnna)
        )

        val friendNames = listOf("Sarah Wilson", "Michael Chen", "Emma Thompson", "James Rodriguez", "Lisa Park", "David Kim", "Anna Martinez")

        friends.forEachIndexed { index, friendLayout ->
            friendLayout?.setOnClickListener {
                openChatWithFriend(friendNames[index])
            }
        }
    }

    private fun openChatWithFriend(friendName: String) {
        try {
            // Navigate to chat interface
            val intent = Intent(this, MessageinterfaceActivity::class.java)
            intent.putExtra("friend_name", friendName)
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Failed to open chat with $friendName", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupTabNavigation(
        friendsTab: LinearLayout?,
        hideMessageTab: LinearLayout?,
        revealMessageTab: LinearLayout?,
        profileTab: LinearLayout?
    ) {
        // Friends tab (current tab)
        friendsTab?.setOnClickListener {
            // Already on friends tab
            Toast.makeText(this, "You're already on Friends tab", Toast.LENGTH_SHORT).show()
        }

        // Hide Message tab
        hideMessageTab?.setOnClickListener {
            val intent = Intent(this, HidemessageActivity::class.java)
            startActivity(intent)
        }

        // Reveal Message tab
        revealMessageTab?.setOnClickListener {
            val intent = Intent(this, DecryptmessageActivity::class.java)
            startActivity(intent)
        }

        // Profile tab
        profileTab?.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
    }
}
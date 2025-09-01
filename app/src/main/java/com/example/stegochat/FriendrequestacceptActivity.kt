package com.example.stegochat

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.view.View

class FriendrequestacceptActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.friendrequestaccept)

        // Initialize views
        val backButton = findViewById<ImageView>(R.id.backButton)
        val pendingTab = findViewById<TextView>(R.id.pendingTab)
        val sentTab = findViewById<TextView>(R.id.sentTab)

        // Back button
        backButton?.setOnClickListener {
            finish()
        }

        // Tab functionality
        pendingTab?.setOnClickListener {
            setActiveTab(pendingTab, sentTab)
        }

        sentTab?.setOnClickListener {
            setActiveTab(sentTab, pendingTab)
        }

        // Set up accept/reject buttons for each friend request
        setupFriendRequestButtons()
    }

    private fun setActiveTab(activeTab: TextView, inactiveTab: TextView) {
        activeTab.setBackgroundResource(R.drawable.tab_selected_background)
        activeTab.setTextColor(resources.getColor(android.R.color.holo_green_dark, null))
        
        inactiveTab.background = null
        inactiveTab.setTextColor(resources.getColor(android.R.color.darker_gray, null))
    }

    private fun setupFriendRequestButtons() {
        // Find all accept and reject buttons
        val acceptButtons = listOf(
            findViewById<Button>(R.id.acceptAarav),
            findViewById<Button>(R.id.acceptButton), // Kiara
            findViewById<Button>(R.id.acceptButton), // Jay
            findViewById<Button>(R.id.acceptButton), // Riya
            findViewById<Button>(R.id.acceptButton)  // Devansh
        )

        val rejectButtons = listOf(
            findViewById<Button>(R.id.rejectAarav),
            findViewById<Button>(R.id.rejectButton), // Kiara
            findViewById<Button>(R.id.rejectButton), // Jay
            findViewById<Button>(R.id.rejectButton), // Riya
            findViewById<Button>(R.id.rejectButton)  // Devansh
        )

        val friendNames = listOf("Aarav Patel", "Kiara Sharma", "Jay Mehta", "Riya Verma", "Devansh Nair")

        // Set up accept buttons
        acceptButtons.forEachIndexed { index, button ->
            button?.setOnClickListener {
                handleAcceptRequest(friendNames[index])
            }
        }

        // Set up reject buttons
        rejectButtons.forEachIndexed { index, button ->
            button?.setOnClickListener {
                handleRejectRequest(friendNames[index])
            }
        }
    }

    private fun handleAcceptRequest(friendName: String) {
        try {
            // Show success message
            Toast.makeText(this, "Friend request accepted!", Toast.LENGTH_SHORT).show()
            
            // Navigate to friend chat
            val intent = Intent(this, FriendchatActivity::class.java)
            intent.putExtra("friend_name", friendName)
            intent.putExtra("friend_status", "accepted")
            startActivity(intent)
            
            // Remove the friend request from the list (in a real app, this would update the database)
            removeFriendRequest(friendName)
            
        } catch (e: Exception) {
            Toast.makeText(this, "Failed to accept request", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleRejectRequest(friendName: String) {
        try {
            // Show rejection message
            Toast.makeText(this, "Friend request rejected", Toast.LENGTH_SHORT).show()
            
            // Remove the friend request from the list
            removeFriendRequest(friendName)
            
        } catch (e: Exception) {
            Toast.makeText(this, "Failed to reject request", Toast.LENGTH_SHORT).show()
        }
    }

    private fun removeFriendRequest(friendName: String) {
        // In a real app, this would update the database
        // For now, we'll just show a message
        Toast.makeText(this, "$friendName's request removed", Toast.LENGTH_SHORT).show()
        
        // You could also hide the specific friend request view here
        // For example: findViewById<LinearLayout>(R.id.friendRequestAarav)?.visibility = View.GONE
    }
}
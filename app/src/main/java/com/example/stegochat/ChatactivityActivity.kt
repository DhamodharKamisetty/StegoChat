package com.example.stegochat

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ChatactivityActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chatactivity)

        // Get friend name from intent
        val friendName = intent.getStringExtra("friend_name") ?: "Friend"

        // Initialize views
        val backButton = findViewById<ImageView>(R.id.backButton)
        val messageInput = findViewById<EditText>(R.id.messageInput)
        val sendButton = findViewById<Button>(R.id.sendButton)
        
        // Chat options
        val pinConversationLayout = findViewById<LinearLayout>(R.id.pinConversationLayout)
        val archiveChatLayout = findViewById<LinearLayout>(R.id.archiveChatLayout)
        val changeWallpaperLayout = findViewById<LinearLayout>(R.id.changeWallpaperLayout)
        val deleteConversationLayout = findViewById<LinearLayout>(R.id.deleteConversationLayout)
        val cancelButton = findViewById<Button>(R.id.cancelButton)

        // Back Button Click
        backButton?.setOnClickListener {
            finish() // Go back to friends list
        }

        // Send Button Click
        sendButton?.setOnClickListener {
            val message = messageInput?.text.toString().trim()
            if (message.isNotEmpty()) {
                // TODO: Implement actual message sending
                Toast.makeText(this, "Message sent to $friendName", Toast.LENGTH_SHORT).show()
                messageInput?.text?.clear()
            } else {
                Toast.makeText(this, "Please enter a message", Toast.LENGTH_SHORT).show()
            }
        }

        // Pin Conversation
        pinConversationLayout?.setOnClickListener {
            startActivity(Intent(this, PinconversationActivity::class.java))
        }

        // Archive Chat
        archiveChatLayout?.setOnClickListener {
            startActivity(Intent(this, ArchivechatActivity::class.java))
        }

        // Change Wallpaper
        changeWallpaperLayout?.setOnClickListener {
            // TODO: Implement wallpaper change functionality
            Toast.makeText(this, "Wallpaper change coming soon!", Toast.LENGTH_SHORT).show()
        }

        // Delete Conversation
        deleteConversationLayout?.setOnClickListener {
            startActivity(Intent(this, DeleteconversationActivity::class.java))
        }

        // Cancel Button
        cancelButton?.setOnClickListener {
            finish()
        }
    }
}
package com.example.stegochat

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ThreedotsActivity2 : AppCompatActivity() {
    
    private var messageId: String? = null
    private var messageText: String? = null
    private var isMessageContext: Boolean = false
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.threedots2)

        // Get message context from intent
        messageId = intent.getStringExtra("message_id")
        messageText = intent.getStringExtra("message_text")
        isMessageContext = intent.getBooleanExtra("is_message_context", false)

        // Initialize views
        val backButton = findViewById<ImageView>(R.id.backButton)
        val cancelButton = findViewById<Button>(R.id.cancelButton)
        
        // Chat options
        val encryptChatOption = findViewById<LinearLayout>(R.id.encryptChatOption)
        val pinConversationOption = findViewById<LinearLayout>(R.id.pinConversationOption)
        val archiveChatOption = findViewById<LinearLayout>(R.id.archiveChatOption)
        val changeWallpaperOption = findViewById<LinearLayout>(R.id.changeWallpaperOption)
        val deleteConversationOption = findViewById<LinearLayout>(R.id.deleteConversationOption)
        
        // Update dialog title if it's message context
        if (isMessageContext) {
            val dialogTitle = findViewById<TextView>(R.id.dialogTitle)
            dialogTitle?.text = "Message Options"
        }
        
        // Back Button Click
        backButton?.setOnClickListener {
            finish() // Go back to previous activity
        }
        
        // Cancel Button Click
        cancelButton?.setOnClickListener {
            finish() // Close the dialog
        }
        
        // Encrypt Chat Option
        encryptChatOption?.setOnClickListener {
            if (isMessageContext) {
                // Return encrypt action for message
                returnAction("encrypt")
            } else {
                // Navigate to encrypt chat activity
                val intent = Intent(this, EncryptchatoptionActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        
        // Pin Conversation Option
        pinConversationOption?.setOnClickListener {
            if (isMessageContext) {
                // Return pin action for message
                returnAction("pin")
            } else {
                // Navigate to pin conversation activity
                val intent = Intent(this, PinconversationActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        
        // Archive Chat Option
        archiveChatOption?.setOnClickListener {
            if (isMessageContext) {
                // Return archive action for message
                returnAction("archive")
            } else {
                // Navigate to archive chat activity
                val intent = Intent(this, ArchivechatActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        
        // Change Wallpaper Option
        changeWallpaperOption?.setOnClickListener {
            if (isMessageContext) {
                // Return wallpaper action for message (if applicable)
                Toast.makeText(this, "Wallpaper change not applicable to messages", Toast.LENGTH_SHORT).show()
            } else {
                // TODO: Implement wallpaper change functionality
                Toast.makeText(this, "Wallpaper change feature coming soon!", Toast.LENGTH_SHORT).show()
            }
        }
        
        // Delete Conversation Option
        deleteConversationOption?.setOnClickListener {
            if (isMessageContext) {
                // Return delete action for message
                returnAction("delete")
            } else {
                // Navigate to delete conversation activity
                val intent = Intent(this, DeleteconversationActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
    
    private fun returnAction(action: String) {
        val resultIntent = Intent()
        resultIntent.putExtra("action", action)
        resultIntent.putExtra("message_id", messageId)
        resultIntent.putExtra("message_text", messageText)
        setResult(RESULT_OK, resultIntent)
        finish()
    }
}
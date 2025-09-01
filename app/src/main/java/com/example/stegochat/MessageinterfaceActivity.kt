package com.example.stegochat

import android.content.Intent
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class MessageinterfaceActivity : AppCompatActivity() {
    
    private val imageSelectionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.let { intent ->
                val selectedImageIndex = intent.getIntExtra("selected_image_index", -1)
                val selectedImageId = intent.getIntExtra("selected_image_id", -1)
                
                if (selectedImageIndex != -1 && selectedImageId != -1) {
                    // Handle the selected image
                    Toast.makeText(this, "Image ${selectedImageIndex + 1} attached to message", Toast.LENGTH_SHORT).show()
                    
                    // TODO: You can store the selected image data and display it in the message interface
                    // For example, you could add the image to a list of attachments
                }
            }
        }
    }
    
    private val chatOptionsLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            result.data?.let { intent ->
                val action = intent.getStringExtra("action")
                val messageId = intent.getStringExtra("message_id")
                
                when (action) {
                    "delete" -> {
                        // Handle message deletion
                        Toast.makeText(this, "Message deleted", Toast.LENGTH_SHORT).show()
                        // TODO: Remove the message from the UI
                    }
                    "archive" -> {
                        // Handle message archiving
                        Toast.makeText(this, "Message archived", Toast.LENGTH_SHORT).show()
                        // TODO: Archive the message
                    }
                    "pin" -> {
                        // Handle message pinning
                        Toast.makeText(this, "Message pinned", Toast.LENGTH_SHORT).show()
                        // TODO: Pin the message
                    }
                    "encrypt" -> {
                        // Handle message encryption
                        Toast.makeText(this, "Message encryption enabled", Toast.LENGTH_SHORT).show()
                        // TODO: Enable encryption for the message
                    }
                }
            }
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.messageinterface)

        // Get the friend name from intent
        val friendName = intent.getStringExtra("friend_name") ?: "Friend"
        
        // Initialize views
        val backButton = findViewById<ImageView>(R.id.backButton)
        val videoCallButton = findViewById<ImageView>(R.id.videoCallButton)
        val moreOptionsButton = findViewById<ImageView>(R.id.moreOptionsButton)
        val attachButton = findViewById<ImageView>(R.id.attachButton)
        val sendButton = findViewById<ImageView>(R.id.sendButton)
        val messageInput = findViewById<EditText>(R.id.messageInput)
        
        // Initialize message containers for long press
        val incomingMessage1 = findViewById<LinearLayout>(R.id.incomingMessage1)
        val outgoingMessage1 = findViewById<LinearLayout>(R.id.outgoingMessage1)
        val imageMessage1 = findViewById<LinearLayout>(R.id.imageMessage1)
        val hiddenMessage1 = findViewById<LinearLayout>(R.id.hiddenMessage1)
        val responseMessage1 = findViewById<LinearLayout>(R.id.responseMessage1)
        
        // Show a toast with the friend name for debugging
        Toast.makeText(this, "Chat with $friendName", Toast.LENGTH_SHORT).show()

        // Back Button Click (header back arrow)
        backButton?.setOnClickListener {
            // Navigate back to friend chat
            finish()
        }

        // Video Call Button Click
        videoCallButton?.setOnClickListener {
            // TODO: Implement video call functionality
            Toast.makeText(this, "Video call feature coming soon!", Toast.LENGTH_SHORT).show()
        }

        // More Options Button Click
        moreOptionsButton?.setOnClickListener {
            // Navigate to three dots options activity
            val intent = Intent(this, ThreedotsActivity2::class.java)
            startActivity(intent)
        }

        // Attach Button Click
        attachButton?.setOnClickListener {
            // Navigate to image selection activity for result
            val intent = Intent(this, Chooseimage1Activity::class.java)
            imageSelectionLauncher.launch(intent)
        }

        // Send Button Click
        sendButton?.setOnClickListener {
            val message = messageInput?.text.toString().trim()
            if (message.isNotEmpty()) {
                // TODO: Implement message sending functionality
                Toast.makeText(this, "Message sent!", Toast.LENGTH_SHORT).show()
                messageInput?.text?.clear()
            } else {
                Toast.makeText(this, "Please enter a message", Toast.LENGTH_SHORT).show()
            }
        }
        
        // Set up long press listeners for all messages
        setupMessageLongPress(incomingMessage1, "incoming_message_1", "Hey, how are you?")
        setupMessageLongPress(outgoingMessage1, "outgoing_message_1", "I'm good! Just finished work.")
        setupMessageLongPress(imageMessage1, "image_message_1", "Image message")
        setupMessageLongPress(hiddenMessage1, "hidden_message_1", "Check this hidden message")
        setupMessageLongPress(responseMessage1, "response_message_1", "Got it, let me decode!")
    }
    
    private fun setupMessageLongPress(messageContainer: LinearLayout?, messageId: String, messageText: String) {
        messageContainer?.let { container ->
            container.setOnLongClickListener {
                // Navigate to chat options with message context
                val intent = Intent(this, ThreedotsActivity2::class.java)
                intent.putExtra("message_id", messageId)
                intent.putExtra("message_text", messageText)
                intent.putExtra("is_message_context", true)
                chatOptionsLauncher.launch(intent)
                true // Return true to indicate the long press was handled
            }
        }
    }
}
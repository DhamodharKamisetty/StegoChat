package com.example.stegochat

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FirestoreExampleActivity : AppCompatActivity() {
    
    private lateinit var firebaseHelper: FirebaseDatabaseHelper
    private lateinit var statusText: TextView
    private lateinit var messageInput: EditText
    private lateinit var sendMessageButton: Button
    private lateinit var createProfileButton: Button
    private lateinit var getProfileButton: Button
    private lateinit var updateStatusButton: Button
    
    // Realtime listener state
    private var messagesListener: ListenerRegistration? = null
    private var activeConversationId: String? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firestore_example)
        
        firebaseHelper = FirebaseDatabaseHelper()
        
        // Initialize views
        statusText = findViewById(R.id.statusText)
        messageInput = findViewById(R.id.messageInput)
        sendMessageButton = findViewById(R.id.sendMessageButton)
        createProfileButton = findViewById(R.id.createProfileButton)
        getProfileButton = findViewById(R.id.getProfileButton)
        updateStatusButton = findViewById(R.id.updateStatusButton)
        
        // Set up button click listeners
        sendMessageButton.setOnClickListener { sendTestMessage() }
        createProfileButton.setOnClickListener { createUserProfile() }
        getProfileButton.setOnClickListener { getUserProfile() }
        updateStatusButton.setOnClickListener { updateUserStatus() }
        
        // Check authentication status
        checkAuthStatus()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        stopMessagesListener()
    }
    
    private fun checkAuthStatus() {
        val isAuthenticated = firebaseHelper.isUserAuthenticated()
        val currentUser = firebaseHelper.getCurrentUser()
        
        if (isAuthenticated && currentUser != null) {
            statusText.text = "✅ Authenticated as: ${currentUser.email}"
        } else {
            statusText.text = "❌ Not authenticated. Please login first."
        }
    }
    
    private fun createUserProfile() {
        val currentUser = firebaseHelper.getCurrentUser()
        if (currentUser == null) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show()
            return
        }
        
        CoroutineScope(Dispatchers.Main).launch {
            statusText.text = "Creating user profile..."
            
            val success = firebaseHelper.createUserProfile(
                userId = currentUser.uid,
                email = currentUser.email ?: "",
                displayName = currentUser.displayName ?: "User"
            )
            
            if (success) {
                statusText.text = "✅ User profile created successfully!"
                Toast.makeText(this@FirestoreExampleActivity, "Profile created!", Toast.LENGTH_SHORT).show()
            } else {
                statusText.text = "❌ Failed to create user profile"
                Toast.makeText(this@FirestoreExampleActivity, "Profile creation failed", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun getUserProfile() {
        val currentUserId = firebaseHelper.getCurrentUserId()
        if (currentUserId == null) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show()
            return
        }
        
        CoroutineScope(Dispatchers.Main).launch {
            statusText.text = "Getting user profile..."
            
            val profile = firebaseHelper.getUserProfile(currentUserId)
            
            if (profile != null) {
                val displayName = profile["displayName"] as? String ?: "Unknown"
                val email = profile["email"] as? String ?: "No email"
                val status = profile["status"] as? String ?: "No status"
                
                statusText.text = "✅ Profile loaded!\n" +
                    "Name: $displayName\n" +
                    "Email: $email\n" +
                    "Status: $status"
            } else {
                statusText.text = "❌ Profile not found. Create one first!"
            }
        }
    }
    
    private fun updateUserStatus() {
        val currentUserId = firebaseHelper.getCurrentUserId()
        if (currentUserId == null) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show()
            return
        }
        
        CoroutineScope(Dispatchers.Main).launch {
            statusText.text = "Updating user status..."
            
            val updates = mapOf(
                "status" to "Updated via Firestore Example!",
                "lastSeen" to com.google.firebase.Timestamp.now()
            )
            
            val success = firebaseHelper.updateUserProfile(currentUserId, updates)
            
            if (success) {
                statusText.text = "✅ User status updated successfully!"
                Toast.makeText(this@FirestoreExampleActivity, "Status updated!", Toast.LENGTH_SHORT).show()
            } else {
                statusText.text = "❌ Failed to update user status"
                Toast.makeText(this@FirestoreExampleActivity, "Update failed", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun sendTestMessage() {
        val currentUserId = firebaseHelper.getCurrentUserId()
        if (currentUserId == null) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show()
            return
        }
        
        val message = messageInput.text.toString().trim()
        if (message.isEmpty()) {
            Toast.makeText(this, "Please enter a message", Toast.LENGTH_SHORT).show()
            return
        }
        
        CoroutineScope(Dispatchers.Main).launch {
            try {
                statusText.text = "Creating conversation..."
                
                // Create a test conversation with yourself (for demo purposes)
                val conversationId = firebaseHelper.getOrCreateConversation(currentUserId, currentUserId)
                
                if (conversationId.isNotEmpty()) {
                    // Start realtime listener once per conversation
                    if (activeConversationId != conversationId) {
                        startMessagesListener(conversationId)
                    }
                    
                    statusText.text = "Conversation ready! Sending message..."
                    
                    val success = firebaseHelper.sendMessage(
                        conversationId = conversationId,
                        senderId = currentUserId,
                        message = message
                    )
                    
                    if (success) {
                        messageInput.text.clear()
                        fetchAndShowLastMessage(conversationId)
                        Toast.makeText(this@FirestoreExampleActivity, "Message sent!", Toast.LENGTH_SHORT).show()
                    } else {
                        statusText.text = "❌ Failed to send message\nConversation ID: $conversationId"
                        Toast.makeText(this@FirestoreExampleActivity, "Send failed", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    statusText.text = "❌ Failed to create conversation\nUser ID: $currentUserId\nCheck:\n1. Firestore enabled\n2. Security rules\n3. Internet connection"
                    Toast.makeText(this@FirestoreExampleActivity, "Conversation creation failed", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                statusText.text = "❌ Error: ${e.message}\n\nCheck Logcat for details"
                Toast.makeText(this@FirestoreExampleActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
    
    private fun startMessagesListener(conversationId: String) {
        stopMessagesListener()
        activeConversationId = conversationId
        val db = FirebaseFirestore.getInstance()
        messagesListener = db.collection("messages")
            .whereEqualTo("conversationId", conversationId)
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null || snapshot == null) {
                    statusText.text = "❌ Listener error: ${e?.message ?: "no data"}"
                    return@addSnapshotListener
                }
                val messages = snapshot.documents.mapNotNull { it.getString("message") }
                val last = messages.lastOrNull().orEmpty()
                statusText.text = "💬 Messages (${messages.size})\nLast: $last"
            }
    }
    
    private fun stopMessagesListener() {
        messagesListener?.remove()
        messagesListener = null
        activeConversationId = null
    }
    
    private fun fetchAndShowLastMessage(conversationId: String) {
        CoroutineScope(Dispatchers.Main).launch {
            val msgs = firebaseHelper.getConversationMessages(conversationId)
            val lastMsg = (msgs.lastOrNull()?.get("message") as? String).orEmpty()
            statusText.text = "✅ Saved to Firestore.\nTotal: ${msgs.size}\nLast: $lastMsg"
        }
    }
}

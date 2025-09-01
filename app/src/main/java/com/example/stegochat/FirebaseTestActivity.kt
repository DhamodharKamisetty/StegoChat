package com.example.stegochat

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FirebaseTestActivity : AppCompatActivity() {
    
    private lateinit var firebaseHelper: FirebaseDatabaseHelper
    private lateinit var statusText: TextView
    private lateinit var testAuthButton: Button
    private lateinit var testUserButton: Button
    private lateinit var testFriendButton: Button
    private lateinit var testChatButton: Button
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firebase_test)
        
        firebaseHelper = FirebaseDatabaseHelper()
        
        // Initialize views
        statusText = findViewById(R.id.statusText)
        testAuthButton = findViewById(R.id.testAuthButton)
        testUserButton = findViewById(R.id.testUserButton)
        testFriendButton = findViewById(R.id.testFriendButton)
        testChatButton = findViewById(R.id.testChatButton)
        
        // Test Firebase connection
        testFirebaseConnection()
        
        // Set up button click listeners
        testAuthButton.setOnClickListener { testAuthentication() }
        testUserButton.setOnClickListener { testUserManagement() }
        testFriendButton.setOnClickListener { testFriendSystem() }
        testChatButton.setOnClickListener { testChatSystem() }
    }
    
    private fun testFirebaseConnection() {
        statusText.text = "Testing Firebase connection..."
        firebaseHelper.testFirebaseConnection()
        statusText.text = "Firebase connection test completed. Check logs for details."
    }
    
    private fun testAuthentication() {
        CoroutineScope(Dispatchers.Main).launch {
            statusText.text = "Testing Authentication..."
            
            val isAuthenticated = firebaseHelper.isUserAuthenticated()
            val currentUser = firebaseHelper.getCurrentUser()
            
            val result = if (isAuthenticated && currentUser != null) {
                "✅ Authentication Working!\n" +
                "User ID: ${currentUser.uid}\n" +
                "Email: ${currentUser.email}"
            } else {
                "❌ No user authenticated\n" +
                "Please login first"
            }
            
            statusText.text = result
            Toast.makeText(this@FirebaseTestActivity, 
                if (isAuthenticated) "Authentication: SUCCESS" else "Authentication: FAILED", 
                Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun testUserManagement() {
        CoroutineScope(Dispatchers.Main).launch {
            statusText.text = "Testing User Management..."
            
            val currentUserId = firebaseHelper.getCurrentUserId()
            if (currentUserId == null) {
                statusText.text = "❌ No authenticated user"
                return@launch
            }
            
            try {
                // Get user profile
                val userProfile = firebaseHelper.getUserProfile(currentUserId)
                
                // Get all users
                val allUsers = firebaseHelper.getAllUsers()
                
                val result = if (userProfile != null) {
                    "✅ User Management Working!\n" +
                    "Profile: ${userProfile["displayName"]}\n" +
                    "Email: ${userProfile["email"]}\n" +
                    "Total Users: ${allUsers.size}"
                } else {
                    "❌ User profile not found"
                }
                
                statusText.text = result
                Toast.makeText(this@FirebaseTestActivity, 
                    if (userProfile != null) "User Management: SUCCESS" else "User Management: FAILED", 
                    Toast.LENGTH_SHORT).show()
                    
            } catch (e: Exception) {
                statusText.text = "❌ User Management Error: ${e.message}"
                Toast.makeText(this@FirebaseTestActivity, "User Management: ERROR", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun testFriendSystem() {
        CoroutineScope(Dispatchers.Main).launch {
            statusText.text = "Testing Friend System..."
            
            val currentUserId = firebaseHelper.getCurrentUserId()
            if (currentUserId == null) {
                statusText.text = "❌ No authenticated user"
                return@launch
            }
            
            try {
                // Get pending friend requests
                val pendingRequests = firebaseHelper.getPendingFriendRequests(currentUserId)
                
                // Get user's friends
                val friends = firebaseHelper.getUserFriends(currentUserId)
                
                val result = "✅ Friend System Working!\n" +
                    "Pending Requests: ${pendingRequests.size}\n" +
                    "Friends: ${friends.size}"
                
                statusText.text = result
                Toast.makeText(this@FirebaseTestActivity, "Friend System: SUCCESS", Toast.LENGTH_SHORT).show()
                
            } catch (e: Exception) {
                statusText.text = "❌ Friend System Error: ${e.message}"
                Toast.makeText(this@FirebaseTestActivity, "Friend System: ERROR", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun testChatSystem() {
        CoroutineScope(Dispatchers.Main).launch {
            statusText.text = "Testing Chat System..."
            
            val currentUserId = firebaseHelper.getCurrentUserId()
            if (currentUserId == null) {
                statusText.text = "❌ No authenticated user"
                return@launch
            }
            
            try {
                // Get user's conversations
                val conversations = firebaseHelper.getUserConversations(currentUserId)
                
                val result = "✅ Chat System Working!\n" +
                    "Conversations: ${conversations.size}"
                
                statusText.text = result
                Toast.makeText(this@FirebaseTestActivity, "Chat System: SUCCESS", Toast.LENGTH_SHORT).show()
                
            } catch (e: Exception) {
                statusText.text = "❌ Chat System Error: ${e.message}"
                Toast.makeText(this@FirebaseTestActivity, "Chat System: ERROR", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

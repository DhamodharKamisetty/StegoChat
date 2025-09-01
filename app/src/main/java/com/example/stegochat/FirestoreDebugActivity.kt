package com.example.stegochat

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class FirestoreDebugActivity : AppCompatActivity() {
    
    private lateinit var firebaseHelper: FirebaseDatabaseHelper
    private lateinit var statusText: TextView
    private lateinit var testButton: Button
    private lateinit var firestore: FirebaseFirestore
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firestore_debug)
        
        firebaseHelper = FirebaseDatabaseHelper()
        firestore = FirebaseFirestore.getInstance()
        
        statusText = findViewById(R.id.statusText)
        testButton = findViewById(R.id.testButton)
        
        testButton.setOnClickListener { runDebugTest() }
        
        // Run initial test
        runDebugTest()
    }
    
    private fun runDebugTest() {
        statusText.text = "Running debug test..."
        
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val currentUser = firebaseHelper.getCurrentUser()
                if (currentUser == null) {
                    statusText.text = "❌ NOT AUTHENTICATED\nPlease login first"
                    return@launch
                }
                
                val userId = currentUser.uid
                statusText.text = "✅ Authenticated: ${currentUser.email}\nUser ID: $userId\n\nTesting Firestore..."
                
                // Test 1: Basic Firestore connection
                try {
                    firestore.collection("test").document("debug_test")
                        .set(mapOf("timestamp" to com.google.firebase.Timestamp.now()))
                        .await()
                    
                    statusText.text = statusText.text.toString() + "\n✅ Basic Firestore write successful"
                } catch (e: Exception) {
                    statusText.text = statusText.text.toString() + "\n❌ Basic Firestore write failed: ${e.message}"
                    return@launch
                }
                
                // Test 2: Try to create a simple conversation
                try {
                    val conversationData = mapOf(
                        "participants" to listOf(userId, userId),
                        "createdAt" to com.google.firebase.Timestamp.now(),
                        "lastMessage" to "",
                        "lastMessageTime" to com.google.firebase.Timestamp.now()
                    )
                    
                    val docRef = firestore.collection("conversations")
                        .add(conversationData)
                        .await()
                    
                    statusText.text = statusText.text.toString() + "\n✅ Conversation creation successful!\nConversation ID: ${docRef.id}"
                    
                    // Test 3: Try to send a message
                    try {
                        val messageData = mapOf(
                            "conversationId" to docRef.id,
                            "senderId" to userId,
                            "message" to "Debug test message",
                            "messageType" to "text",
                            "timestamp" to com.google.firebase.Timestamp.now(),
                            "isRead" to false
                        )
                        
                        firestore.collection("messages")
                            .add(messageData)
                            .await()
                        
                        statusText.text = statusText.text.toString() + "\n✅ Message sending successful!"
                        
                        // Clean up test data
                        firestore.collection("conversations").document(docRef.id).delete()
                        
                    } catch (e: Exception) {
                        statusText.text = statusText.text.toString() + "\n❌ Message sending failed: ${e.message}"
                    }
                    
                } catch (e: Exception) {
                    statusText.text = statusText.text.toString() + "\n❌ Conversation creation failed: ${e.message}"
                    Log.e("FirestoreDebug", "Conversation creation error", e)
                }
                
            } catch (e: Exception) {
                statusText.text = "❌ Debug test failed: ${e.message}"
                Log.e("FirestoreDebug", "Debug test error", e)
            }
        }
    }
}

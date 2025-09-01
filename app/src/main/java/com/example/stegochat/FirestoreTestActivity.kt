package com.example.stegochat

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class FirestoreTestActivity : AppCompatActivity() {
    
    private lateinit var firebaseHelper: FirebaseDatabaseHelper
    private lateinit var statusText: TextView
    private lateinit var testButton: Button
    private lateinit var fixButton: Button
    private lateinit var firestore: FirebaseFirestore
    private lateinit var messageInput: EditText
    private lateinit var sendButton: Button
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firestore_test)
        
        firebaseHelper = FirebaseDatabaseHelper()
        firestore = FirebaseFirestore.getInstance()
        
        statusText = findViewById(R.id.statusText)
        testButton = findViewById(R.id.testButton)
        fixButton = findViewById(R.id.fixButton)
        messageInput = findViewById(R.id.messageInput)
        sendButton = findViewById(R.id.sendButton)
        
        testButton.setOnClickListener { runComprehensiveTest() }
        fixButton.setOnClickListener { showFixInstructions() }
        sendButton.setOnClickListener { sendOneOffMessage() }
        
        // Run initial test
        runComprehensiveTest()
    }
    
    private fun sendOneOffMessage() {
        val text = messageInput.text.toString().trim()
        if (text.isEmpty()) {
            Toast.makeText(this, "Type a message", Toast.LENGTH_SHORT).show()
            return
        }
        val user = firebaseHelper.getCurrentUser()
        if (user == null) {
            Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show()
            return
        }
        CoroutineScope(Dispatchers.Main).launch {
            try {
                statusText.text = "Creating conversation for test..."
                val conversationId = firebaseHelper.getOrCreateConversation(user.uid, user.uid)
                if (conversationId.isEmpty()) {
                    statusText.text = "❌ Could not create conversation"
                    return@launch
                }
                val ok = firebaseHelper.sendMessage(conversationId, user.uid, text)
                if (ok) {
                    statusText.text = "✅ Message stored in Firestore\nConversation: $conversationId\nMessage: $text"
                    messageInput.text.clear()
                } else {
                    statusText.text = "❌ Message send failed"
                }
            } catch (e: Exception) {
                statusText.text = "❌ Error: ${e.message}"
            }
        }
    }

    private fun runComprehensiveTest() {
        statusText.text = "🔍 Running comprehensive Firestore test..."
        
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val currentUser = firebaseHelper.getCurrentUser()
                if (currentUser == null) {
                    statusText.text = "❌ NOT AUTHENTICATED\n\nPlease login first!\n\nTap 'Fix Issues' for help."
                    return@launch
                }
                
                val userId = currentUser.uid
                var testResults = "✅ Authenticated: ${currentUser.email}\nUser ID: $userId\n\n"
                
                // Test 1: Basic Firestore connection
                try {
                    firestore.collection("test").document("connection_test")
                        .set(mapOf("timestamp" to com.google.firebase.Timestamp.now()))
                        .await()
                    
                    testResults += "✅ Basic Firestore write: SUCCESS\n"
                } catch (e: Exception) {
                    testResults += "❌ Basic Firestore write: FAILED\nError: ${e.message}\n\n"
                    testResults += "🔧 SOLUTION: Enable Firestore Database in Firebase Console\n"
                    statusText.text = testResults
                    return@launch
                }
                
                // Test 2: Create user profile
                try {
                    val success = firebaseHelper.createUserProfile(
                        userId = userId,
                        email = currentUser.email ?: "",
                        displayName = currentUser.displayName ?: "User"
                    )
                    
                    if (success) {
                        testResults += "✅ User profile creation: SUCCESS\n"
                    } else {
                        testResults += "❌ User profile creation: FAILED\n"
                    }
                } catch (e: Exception) {
                    testResults += "❌ User profile creation: FAILED\nError: ${e.message}\n"
                }
                
                // Test 3: Get user profile
                try {
                    val profile = firebaseHelper.getUserProfile(userId)
                    if (profile != null) {
                        testResults += "✅ User profile retrieval: SUCCESS\n"
                    } else {
                        testResults += "❌ User profile retrieval: FAILED\n"
                    }
                } catch (e: Exception) {
                    testResults += "❌ User profile retrieval: FAILED\nError: ${e.message}\n"
                }
                
                // Test 4: Create conversation
                try {
                    val conversationId = firebaseHelper.getOrCreateConversation(userId, userId)
                    if (conversationId.isNotEmpty()) {
                        testResults += "✅ Conversation creation: SUCCESS\nConversation ID: $conversationId\n"
                        
                        // Test 5: Send message
                        try {
                            val messageSuccess = firebaseHelper.sendMessage(
                                conversationId = conversationId,
                                senderId = userId,
                                message = "Test message from comprehensive test"
                            )
                            
                            if (messageSuccess) {
                                testResults += "✅ Message sending: SUCCESS\n"
                            } else {
                                testResults += "❌ Message sending: FAILED\n"
                            }
                        } catch (e: Exception) {
                            testResults += "❌ Message sending: FAILED\nError: ${e.message}\n"
                        }
                    } else {
                        testResults += "❌ Conversation creation: FAILED\n"
                    }
                } catch (e: Exception) {
                    testResults += "❌ Conversation creation: FAILED\nError: ${e.message}\n"
                }
                
                // Summary
                if (testResults.contains("❌")) {
                    testResults += "\n🔧 Some tests failed. Tap 'Fix Issues' for solutions."
                } else {
                    testResults += "\n🎉 ALL TESTS PASSED! Firestore is working perfectly!"
                }
                
                statusText.text = testResults
                
            } catch (e: Exception) {
                statusText.text = "❌ Test failed: ${e.message}\n\nTap 'Fix Issues' for help."
                Log.e("FirestoreTest", "Test error", e)
            }
        }
    }
    
    private fun showFixInstructions() {
        val instructions = """
🔧 FIREBASE FIRESTORE FIX INSTRUCTIONS

1. 🔥 ENABLE FIRESTORE DATABASE:
   • Go to: https://console.firebase.google.com
   • Select project: stegochat-f8f9b
   • Click "Firestore Database" in left sidebar
   • Click "Create Database"
   • Choose "Start in test mode"
   • Select location (closest to you)
   • Click "Done"

2. 🔒 SET SECURITY RULES:
   • In Firestore Database, click "Rules" tab
   • Replace with this code:
   
   rules_version = '2';
   service cloud.firestore {
     match /databases/{database}/documents {
       match /{document=**} {
         allow read, write: if request.auth != null;
       }
     }
   }
   
   • Click "Publish"

3. ✅ VERIFY SETUP:
   • Make sure you're logged in to the app
   • Check internet connection
   • Run the test again

4. 🆘 IF STILL FAILING:
   • Check Logcat for detailed errors
   • Verify google-services.json is correct
   • Ensure Firebase project is active

Tap "Run Test" after completing these steps!
        """.trimIndent()
        
        statusText.text = instructions
    }
}

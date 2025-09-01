package com.example.stegochat

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FirestoreQuickTestActivity : AppCompatActivity() {
    
    private lateinit var firebaseHelper: FirebaseDatabaseHelper
    private lateinit var statusText: TextView
    private lateinit var testButton: Button
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firestore_quick_test)
        
        firebaseHelper = FirebaseDatabaseHelper()
        statusText = findViewById(R.id.statusText)
        testButton = findViewById(R.id.testButton)
        
        // Run initial test
        runQuickTest()
        
        testButton.setOnClickListener {
            runQuickTest()
        }
    }
    
    private fun runQuickTest() {
        statusText.text = "Running Firestore test..."
        
        CoroutineScope(Dispatchers.Main).launch {
            try {
                // Test 1: Check authentication
                val isAuthenticated = firebaseHelper.isUserAuthenticated()
                val currentUser = firebaseHelper.getCurrentUser()
                
                if (!isAuthenticated || currentUser == null) {
                    statusText.text = "❌ NOT AUTHENTICATED\nPlease login first"
                    return@launch
                }
                
                // Test 2: Test Firestore connection
                firebaseHelper.testFirebaseConnection()
                
                // Test 3: Try to create/get user profile
                val userId = currentUser.uid
                var profile = firebaseHelper.getUserProfile(userId)
                
                if (profile == null) {
                    // Create profile if it doesn't exist
                    val success = firebaseHelper.createUserProfile(
                        userId = userId,
                        email = currentUser.email ?: "",
                        displayName = currentUser.displayName ?: "User"
                    )
                    
                    if (success) {
                        profile = firebaseHelper.getUserProfile(userId)
                        statusText.text = "✅ FIRESTORE WORKING!\n" +
                            "User: ${currentUser.email}\n" +
                            "Profile created and loaded\n" +
                            "Display Name: ${profile?.get("displayName")}\n" +
                            "Status: ${profile?.get("status")}"
                    } else {
                        statusText.text = "❌ FAILED TO CREATE PROFILE\nCheck Firebase Console"
                    }
                } else {
                    statusText.text = "✅ FIRESTORE WORKING!\n" +
                        "User: ${currentUser.email}\n" +
                        "Profile loaded successfully\n" +
                        "Display Name: ${profile["displayName"]}\n" +
                        "Status: ${profile["status"]}"
                }
                
                Toast.makeText(this@FirestoreQuickTestActivity, "Firestore test completed!", Toast.LENGTH_SHORT).show()
                
            } catch (e: Exception) {
                Log.e("FirestoreQuickTest", "Error: ${e.message}", e)
                statusText.text = "❌ FIRESTORE ERROR\n${e.message}\n\nCheck:\n1. Internet connection\n2. Firebase Console setup\n3. Security rules"
                Toast.makeText(this@FirestoreQuickTestActivity, "Test failed: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}


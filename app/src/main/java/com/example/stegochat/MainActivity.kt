
package com.example.stegochat

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import com.google.firebase.FirebaseApp

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // Test Firebase connection
        try {
            FirebaseApp.initializeApp(this)
            Log.d("MainActivity", "✅ Firebase initialized successfully")
            
            // Test Firebase services
            val firebaseHelper = FirebaseDatabaseHelper()
            firebaseHelper.testFirebaseConnection()
            
        } catch (e: Exception) {
            Log.e("MainActivity", "❌ Firebase initialization failed: ${e.message}")
        }

        val loginButton = findViewById<Button>(R.id.loginButton)
        val createAccountButton = findViewById<Button>(R.id.createAccountButton)
        val firestoreTestButton = findViewById<Button>(R.id.firestoreTestButton)

        if (loginButton != null) {
            loginButton.setOnClickListener {
                val intent = Intent(this, Loginpage::class.java)
                startActivity(intent)
            }
        } else {
            Log.e("MainActivity", "loginButton not found in layout")
        }

        if (createAccountButton != null) {
            createAccountButton.setOnClickListener {
                val intent = Intent(this, CreateaccountActivity::class.java)
                startActivity(intent)
            }
        } else {
            Log.e("MainActivity", "createAccountButton not found in layout")
        }

        if (firestoreTestButton != null) {
            firestoreTestButton.setOnClickListener {
                val intent = Intent(this, FirestoreTestActivity::class.java)
                startActivity(intent)
            }
        } else {
            Log.e("MainActivity", "firestoreTestButton not found in layout")
        }
    }
}

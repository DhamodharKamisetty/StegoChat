package com.example.stegochat

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import android.util.Log

class CreateaccountActivity : AppCompatActivity() {
    
    private var inviteCode: String? = null
    private var inviterName: String? = null
    
    // Firebase instances
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    
    companion object {
        private const val TAG = "CreateAccountActivity"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.createaccount)
        
        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Get invite data from intent
        inviteCode = intent.getStringExtra("invite_code")
        inviterName = intent.getStringExtra("inviter_name")
        
        // Show invite message if coming from invite link
        if (inviteCode != null) {
            val welcomeMessage = "Welcome! You've been invited by ${inviterName ?: "a friend"}"
            Toast.makeText(this, welcomeMessage, Toast.LENGTH_LONG).show()
        }

        // Initialize views
        val backButton = findViewById<Button>(R.id.backButton)
        val createAccountButton = findViewById<Button>(R.id.createAccountButton)
        val loginLink = findViewById<TextView>(R.id.loginLink)
        val inviteCodeText = findViewById<TextView>(R.id.inviteCodeText)

        // Show invite code if available
        if (inviteCode != null) {
            inviteCodeText?.text = "Invite Code: $inviteCode"
            inviteCodeText?.visibility = android.view.View.VISIBLE
        }

        // Back button
        backButton?.setOnClickListener {
            finish()
        }

        // Create account button
        createAccountButton?.setOnClickListener {
            handleCreateAccount()
        }

        // Login link
        loginLink?.setOnClickListener {
            val intent = Intent(this, Loginpage::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun handleCreateAccount() {
        try {
            // Get form data
            val email = findViewById<EditText>(R.id.emailInput)?.text.toString()
            val password = findViewById<EditText>(R.id.passwordInput)?.text.toString()
            val confirmPassword = findViewById<EditText>(R.id.confirmPasswordInput)?.text.toString()

            // Validate inputs
            if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return
            }

            if (password.length < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                return
            }

            // Show loading
            Toast.makeText(this, "Creating account...", Toast.LENGTH_SHORT).show()

            // Create Firebase user
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        val user = auth.currentUser
                        Log.d(TAG, "createUserWithEmail:success")
                        
                        // Create user profile in Firestore
                        user?.let { firebaseUser ->
                            createUserProfile(firebaseUser, email)
                        }
                        
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.exception)
                        Toast.makeText(this, "Authentication failed: ${task.exception?.message}",
                            Toast.LENGTH_SHORT).show()
                    }
                }

        } catch (e: Exception) {
            Toast.makeText(this, "Failed to create account: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun createUserProfile(user: FirebaseUser, email: String) {
        val userData = hashMapOf(
            "userId" to user.uid,
            "email" to email,
            "displayName" to email.split("@")[0], // Use email prefix as display name
            "createdAt" to com.google.firebase.Timestamp.now(),
            "lastSeen" to com.google.firebase.Timestamp.now(),
            "isOnline" to true
        )
        
        firestore.collection("users")
            .document(user.uid)
            .set(userData)
            .addOnSuccessListener {
                Log.d(TAG, "User profile created successfully")
                
                // Show success message
                val successMessage = if (inviteCode != null) {
                    "Account created successfully! Welcome to StegoChat!"
                } else {
                    "Account created successfully!"
                }
                
                Toast.makeText(this, successMessage, Toast.LENGTH_LONG).show()

                // Navigate to login page after successful account creation
                val intent = Intent(this, Loginpage::class.java).apply {
                    putExtra("account_created", true)
                    putExtra("email", email)
                }
                startActivity(intent)
                finish()
                
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error creating user profile", e)
                Toast.makeText(this, "Account created but profile setup failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
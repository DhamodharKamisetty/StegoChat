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
import com.google.firebase.auth.FirebaseAuth
import android.util.Log

class Loginpage : AppCompatActivity() {
    
    private lateinit var auth: FirebaseAuth
    
    companion object {
        private const val TAG = "Loginpage"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "Loginpage onCreate called")
        setContentView(R.layout.activity_loginpage)
        Log.d(TAG, "Loginpage layout set successfully")
        
        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Initialize views
        val emailInput = findViewById<EditText>(R.id.emailInput)
        val passwordInput = findViewById<EditText>(R.id.passwordInput)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val forgotPassword = findViewById<TextView>(R.id.forgotPassword)
        val signupRedirect = findViewById<TextView>(R.id.signupRedirect)
        
        // Check if coming from account creation (after views are initialized)
        val accountCreated = intent.getBooleanExtra("account_created", false)
        val email = intent.getStringExtra("email")
        
        if (accountCreated && email != null) {
            // Pre-fill email and show welcome message
            emailInput.setText(email)
            Toast.makeText(this, "Account created successfully! Please login with your credentials.", Toast.LENGTH_LONG).show()
        }
        
        // Add password visibility toggle
        val eyeIcon = findViewById<ImageView>(R.id.eyeIcon)
        if (eyeIcon != null) {
            eyeIcon.setOnClickListener {
                if (passwordInput.inputType == android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD) {
                    passwordInput.inputType = android.text.InputType.TYPE_CLASS_TEXT
                    eyeIcon.setImageResource(R.drawable.ic_eye_grey)
                } else {
                    passwordInput.inputType = android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
                    eyeIcon.setImageResource(R.drawable.ic_eye_grey)
                }
            }
        }

        // Login Button Click
        loginButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString()

            if (validateInputs(email, password)) {
                // Show loading
                Toast.makeText(this, "Logging in...", Toast.LENGTH_SHORT).show()
                
                // Authenticate with Firebase
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success")
                            val user = auth.currentUser
                            Toast.makeText(this, "Login successful! Welcome back!", Toast.LENGTH_SHORT).show()
                            
                            // Navigate to friends chat
                            val intent = Intent(this, FriendchatActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.exception)
                            Toast.makeText(this, "Authentication failed: ${task.exception?.message}",
                                Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }

        // If you add a Google sign-in button later, wire its click handler here.

        // Forgot Password Click
        forgotPassword.setOnClickListener {
            val intent = Intent(this, ForgotpasswordActivity::class.java)
            startActivity(intent)
        }

        // Sign Up Redirect Click
        signupRedirect.setOnClickListener {
            val intent = Intent(this, CreateaccountActivity::class.java)
            startActivity(intent)
        }
    }

    private fun validateInputs(email: String, password: String): Boolean {
        if (email.isEmpty()) {
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show()
            return false
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
            return false
        }

        if (password.isEmpty()) {
            Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }
}
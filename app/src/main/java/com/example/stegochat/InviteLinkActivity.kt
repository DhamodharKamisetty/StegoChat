package com.example.stegochat

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class InviteLinkActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invite_link)

        // Get invite data from intent (support both /invite/{code} and ?code= formats)
        val dataUri = intent?.data
        val inviteCode = when {
            dataUri == null -> ""
            !dataUri.path.isNullOrBlank() && dataUri.pathSegments.size >= 2 && dataUri.pathSegments[0] == "invite" -> {
                dataUri.lastPathSegment ?: ""
            }
            else -> dataUri.getQueryParameter("code") ?: ""
        }
        val inviterName = dataUri?.getQueryParameter("from") ?: "Your Friend"

        // Initialize views
        val welcomeText = findViewById<TextView>(R.id.welcomeText)
        val inviteCodeText = findViewById<TextView>(R.id.inviteCodeText)
        val signupButton = findViewById<Button>(R.id.signupButton)
        val loginButton = findViewById<Button>(R.id.loginButton)

        // Update UI with invite information
        welcomeText?.text = "You've been invited to join StegoChat!"
        inviteCodeText?.text = if (inviteCode.isNotBlank()) "Invite Code: $inviteCode" else "Invite Code not found"

        // Sign up button
        signupButton?.setOnClickListener {
            val intent = Intent(this, CreateaccountActivity::class.java).apply {
                putExtra("invite_code", inviteCode)
                putExtra("inviter_name", inviterName)
            }
            startActivity(intent)
            finish()
        }

        // Login button
        loginButton?.setOnClickListener {
            val intent = Intent(this, Loginpage::class.java)
            intent.putExtra("invite_code", inviteCode)
            startActivity(intent)
            finish()
        }

        // Show welcome message
        Toast.makeText(this, "Welcome! You've been invited by $inviterName", Toast.LENGTH_LONG).show()
    }
} 
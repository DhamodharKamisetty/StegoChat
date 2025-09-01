package com.example.stegochat

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class PasswordverificationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.passwordverification)

        // Initialize views
        val backButton = findViewById<ImageView>(R.id.backButton)
        val passwordInput = findViewById<EditText>(R.id.password_input)
        val passwordToggle = findViewById<ImageView>(R.id.password_toggle)
        val continueButton = findViewById<Button>(R.id.btn_continue)
        val cancelButton = findViewById<Button>(R.id.btn_cancel)

        // Back Button Click
        backButton?.setOnClickListener {
            finish() // Go back to previous activity
        }

        // Password Toggle Click
        passwordToggle?.setOnClickListener {
            // TODO: Implement password visibility toggle
            Toast.makeText(this, "Password visibility toggle", Toast.LENGTH_SHORT).show()
        }

        // Continue Button Click
        continueButton?.setOnClickListener {
            val password = passwordInput?.text.toString().trim()
            if (password.isNotEmpty()) {
                // TODO: Implement password verification
                Toast.makeText(this, "Password verified!", Toast.LENGTH_SHORT).show()
                // Navigate to next screen based on context
                finish()
            } else {
                Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show()
            }
        }

        // Cancel Button Click
        cancelButton?.setOnClickListener {
            finish()
        }
    }
}
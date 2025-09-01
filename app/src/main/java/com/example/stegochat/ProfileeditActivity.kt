package com.example.stegochat

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ProfileeditActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profileedit)

        // Initialize views
        val backButton = findViewById<TextView>(R.id.backButton)
        val saveButton = findViewById<Button>(R.id.saveButton)

        // Back Button Click
        backButton?.setOnClickListener {
            finish() // Go back to previous activity
        }

        // Save Button Click
        saveButton?.setOnClickListener {
            Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
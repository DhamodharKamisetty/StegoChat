package com.example.stegochat

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ThreedotsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.threedots)

        // Initialize views
        val backButton = findViewById<ImageView>(R.id.backButton)
        val editOption = findViewById<TextView>(R.id.editOption)
        val deleteOption = findViewById<TextView>(R.id.deleteOption)
        val shareOption = findViewById<TextView>(R.id.shareOption)

        // Back Button Click
        backButton?.setOnClickListener {
            finish()
        }

        // Edit Option
        editOption?.setOnClickListener {
            // TODO: Implement edit functionality
            Toast.makeText(this, "Edit functionality coming soon!", Toast.LENGTH_SHORT).show()
        }

        // Delete Option
        deleteOption?.setOnClickListener {
            // TODO: Implement delete functionality
            Toast.makeText(this, "Delete functionality coming soon!", Toast.LENGTH_SHORT).show()
        }

        // Share Option
        shareOption?.setOnClickListener {
            // TODO: Implement share functionality
            Toast.makeText(this, "Share functionality coming soon!", Toast.LENGTH_SHORT).show()
        }
    }
}
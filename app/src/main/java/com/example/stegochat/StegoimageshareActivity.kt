package com.example.stegochat

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider

class StegoimageshareActivity : AppCompatActivity() {
    private var stegoImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.stegoimageshare)

        // Initialize views
        val viewImageButton = findViewById<Button>(R.id.btnViewImage)
        val shareImageButton = findViewById<Button>(R.id.btnShareImage)
        val backHomeText = findViewById<TextView>(R.id.tvBackHome)

        // Get stego image URI from intent if passed
        intent.getStringExtra("stego_image_uri")?.let { uriString ->
            stegoImageUri = Uri.parse(uriString)
        }

        // View Image Button Click
        viewImageButton?.setOnClickListener {
            stegoImageUri?.let { uri ->
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(uri, "image/*")
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                try {
                    startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(this, "No image viewer app found", Toast.LENGTH_SHORT).show()
                }
            } ?: run {
                Toast.makeText(this, "No image to view", Toast.LENGTH_SHORT).show()
            }
        }

        // Share Image Button Click
        shareImageButton?.setOnClickListener {
            stegoImageUri?.let { uri ->
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "image/*"
                intent.putExtra(Intent.EXTRA_STREAM, uri)
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                try {
                    startActivity(Intent.createChooser(intent, "Share Stego Image"))
                } catch (e: Exception) {
                    Toast.makeText(this, "No sharing app found", Toast.LENGTH_SHORT).show()
                }
            } ?: run {
                Toast.makeText(this, "No image to share", Toast.LENGTH_SHORT).show()
            }
        }

        // Back to Home Click
        backHomeText?.setOnClickListener {
            // Navigate back to friend chat
            val intent = Intent(this, FriendchatActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
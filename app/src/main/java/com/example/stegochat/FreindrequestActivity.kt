package com.example.stegochat

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.content.ClipboardManager
import android.content.ClipData
import android.content.Context
import java.util.UUID

class FreindrequestActivity : AppCompatActivity() {
    
    private val prefs by lazy { getSharedPreferences("settings", MODE_PRIVATE) }
    private val INVITE_CODE: String by lazy {
        // Generate and cache a unique invite code for this user (demo-only)
        val existing = prefs.getString("invite_code", null)
        if (existing != null) existing else UUID.randomUUID().toString().take(8).also {
            prefs.edit().putString("invite_code", it).apply()
        }
    }
    private val USER_NAME: String by lazy {
        prefs.getString("user_display_name", "Your Friend") ?: "Your Friend"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.freindrequest)

        // Initialize views
        val backButton = findViewById<ImageView>(R.id.backButton)
        val copyLinkButton = findViewById<Button>(R.id.copyLinkButton)
        val whatsappShare = findViewById<ImageView>(R.id.whatsappShare)
        val emailShare = findViewById<ImageView>(R.id.emailShare)
        val generalShare = findViewById<ImageView>(R.id.generalShare)
        val inviteLinkText = findViewById<TextView>(R.id.inviteLinkText)

        // Update invite link text
        inviteLinkText?.text = "https://stego.chat/invite/$INVITE_CODE?from=$USER_NAME"

        // Back button
        backButton?.setOnClickListener {
            finish()
        }

        // Copy link functionality
        copyLinkButton?.setOnClickListener {
            copyInviteLink()
        }

        // WhatsApp share
        whatsappShare?.setOnClickListener {
            shareViaWhatsApp()
        }

        // Email share
        emailShare?.setOnClickListener {
            shareViaEmail()
        }

        // General share
        generalShare?.setOnClickListener {
            shareGeneral()
        }
    }

    private fun getInviteLink(): String {
        return "https://stego.chat/invite/$INVITE_CODE?from=$USER_NAME"
    }

    private fun copyInviteLink() {
        try {
            val inviteLink = getInviteLink()
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("StegoChat Invite Link", inviteLink)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this, "Invite link copied to clipboard!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Failed to copy link", Toast.LENGTH_SHORT).show()
        }
    }

    private fun shareViaWhatsApp() {
        try {
            val inviteLink = getInviteLink()
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.setPackage("com.whatsapp")
            intent.putExtra(Intent.EXTRA_TEXT, "Join me on StegoChat! Use this link to sign up: $inviteLink")
            intent.putExtra(Intent.EXTRA_SUBJECT, "StegoChat Invitation")
            
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            } else {
                Toast.makeText(this, "WhatsApp not installed", Toast.LENGTH_SHORT).show()
                shareGeneral()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Failed to share via WhatsApp", Toast.LENGTH_SHORT).show()
            shareGeneral()
        }
    }

    private fun shareViaEmail() {
        try {
            val inviteLink = getInviteLink()
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "message/rfc822"
            intent.putExtra(Intent.EXTRA_SUBJECT, "StegoChat Invitation")
            intent.putExtra(Intent.EXTRA_TEXT, "Hi!\n\nJoin me on StegoChat for secure messaging with steganography!\n\nUse this link to sign up: $inviteLink\n\nBest regards")
            
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(Intent.createChooser(intent, "Send invitation via email"))
            } else {
                Toast.makeText(this, "No email app found", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Failed to share via email", Toast.LENGTH_SHORT).show()
        }
    }

    private fun shareGeneral() {
        try {
            val inviteLink = getInviteLink()
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, "Join me on StegoChat! Use this link to sign up: $inviteLink")
            intent.putExtra(Intent.EXTRA_SUBJECT, "StegoChat Invitation")
            startActivity(Intent.createChooser(intent, "Share invitation"))
        } catch (e: Exception) {
            Toast.makeText(this, "Failed to share", Toast.LENGTH_SHORT).show()
        }
    }
}
package com.example.stegochat

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProfileActivity : AppCompatActivity() {
    private val prefs by lazy { getSharedPreferences("settings", MODE_PRIVATE) }
    private lateinit var firebaseHelper: FirebaseDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            setContentView(R.layout.profile)
            
            // Initialize Firebase helper
            firebaseHelper = FirebaseDatabaseHelper()
            
            // Load user profile from Firestore
            loadUserProfile()
            
            Toast.makeText(this, "Profile screen loaded", Toast.LENGTH_SHORT).show()
            Log.d("ProfileActivity", "Profile screen loaded")
        } catch (e: Exception) {
            Toast.makeText(this, "Error loading profile: ${e.message}", Toast.LENGTH_LONG).show()
            Log.e("ProfileActivity", "Error loading profile", e)
            setContentView(android.R.layout.simple_list_item_1)
            findViewById<TextView>(android.R.id.text1)?.text = "Error loading profile screen. Please contact support."
            return
        }

        try {
            // Back button
            val backButton = findViewById<ImageView>(R.id.back_arrow)
            backButton?.setOnClickListener { finish() }

            // Edit Profile
            val btnEditProfile = findViewById<Button>(R.id.edit_profile)
            btnEditProfile?.setOnClickListener {
                startActivity(Intent(this, ProfileeditActivity::class.java))
            }

            // Change Password
            findViewById<LinearLayout>(R.id.change_password_row)?.setOnClickListener {
                startActivity(Intent(this, ChangepasswordActivity::class.java))
            }

            // Language
            val languageRow = findViewById<LinearLayout>(R.id.language_row)
            languageRow?.setOnClickListener {
                startActivity(Intent(this, LanguageselectionActivity::class.java))
            }

            // Profile Visibility
            val profileVisibilityLayout = findViewById<LinearLayout>(R.id.profile_visibility_row)
            profileVisibilityLayout?.setOnClickListener {
                startActivity(Intent(this, ProfilevisibilityActivity::class.java))
            }

            // Sound & Vibration
            val soundVibrationLayout = findViewById<LinearLayout>(R.id.sound_vibration_row)
            soundVibrationLayout?.setOnClickListener {
                startActivity(Intent(this, SoundandvibrationActivity::class.java))
            }

            // Logout
            val btnLogout = findViewById<Button>(R.id.logout_button)
            btnLogout?.setOnClickListener {
                startActivity(Intent(this, LogoutActivity::class.java))
                finish()
            }

            // Dark mode switch
            val darkModeSwitch = findViewById<Switch>(R.id.dark_mode_switch)
            darkModeSwitch?.isChecked = prefs.getBoolean("dark_mode_enabled", false)
            darkModeSwitch?.setOnCheckedChangeListener { _, isChecked ->
                prefs.edit().putBoolean("dark_mode_enabled", isChecked).apply()
                val mode = if (isChecked) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
                AppCompatDelegate.setDefaultNightMode(mode)
                delegate.applyDayNight()
            }

            // Ensure a default language is set on first run
            if (!prefs.contains("app_language")) {
                prefs.edit().putString("app_language", "en").apply()
            }
            // Initialize language value display
            updateLanguageDisplay()
        } catch (e: Exception) {
            Toast.makeText(this, "Error initializing profile: ${e.message}", Toast.LENGTH_LONG).show()
            Log.e("ProfileActivity", "Error initializing profile", e)
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh language label when returning from selection
        updateLanguageDisplay()
    }

    private fun updateLanguageDisplay() {
        val languageCode = prefs.getString("app_language", "en") ?: "en"
        val languageLabel = when (languageCode) {
            "en" -> "English"
            "hi" -> "हिंदी (Hindi)"
            "ta" -> "தமிழ் (Tamil)"
            "kn" -> "ಕನ್ನಡ (Kannada)"
            "te" -> "తెలుగు (Telugu)"
            "ml" -> "മലയാളം (Malayalam)"
            "bn" -> "বাংলা (Bengali)"
            "gu" -> "ગુજરાતી (Gujarati)"
            else -> "English"
        }
        findViewById<TextView>(R.id.language_value)?.text = languageLabel
    }
    
    /**
     * Load user profile from Firestore
     */
    private fun loadUserProfile() {
        val currentUserId = firebaseHelper.getCurrentUserId()
        if (currentUserId == null) {
            Log.w("ProfileActivity", "No authenticated user found")
            return
        }
        
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val profile = firebaseHelper.getUserProfile(currentUserId)
                
                if (profile != null) {
                    // Update UI with profile data
                    val displayName = profile["displayName"] as? String ?: "User"
                    val email = profile["email"] as? String ?: ""
                    val status = profile["status"] as? String ?: "Hey there! I'm using StegoChat!"
                    
                    // Update profile display name, email, and status
                    findViewById<TextView>(R.id.profile_name)?.text = displayName
                    findViewById<TextView>(R.id.profile_email)?.text = email
                    findViewById<TextView>(R.id.profile_status)?.text = status
                    
                    Log.d("ProfileActivity", "Profile loaded: $displayName")
                } else {
                    // Create profile if it doesn't exist
                    createUserProfile()
                }
            } catch (e: Exception) {
                Log.e("ProfileActivity", "Error loading profile: ${e.message}")
                Toast.makeText(this@ProfileActivity, "Error loading profile", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    /**
     * Create user profile in Firestore
     */
    private fun createUserProfile() {
        val currentUser = firebaseHelper.getCurrentUser()
        if (currentUser == null) {
            Log.w("ProfileActivity", "No authenticated user found")
            return
        }
        
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val success = firebaseHelper.createUserProfile(
                    userId = currentUser.uid,
                    email = currentUser.email ?: "",
                    displayName = currentUser.displayName ?: "User"
                )
                
                if (success) {
                    Log.d("ProfileActivity", "User profile created successfully")
                    // Reload profile after creation
                    loadUserProfile()
                } else {
                    Log.e("ProfileActivity", "Failed to create user profile")
                }
            } catch (e: Exception) {
                Log.e("ProfileActivity", "Error creating profile: ${e.message}")
            }
        }
    }
    
    /**
     * Update user status in Firestore
     */
    private fun updateUserStatus(newStatus: String) {
        val currentUserId = firebaseHelper.getCurrentUserId()
        if (currentUserId == null) {
            Log.w("ProfileActivity", "No authenticated user found")
            return
        }
        
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val updates = mapOf(
                    "status" to newStatus,
                    "lastSeen" to com.google.firebase.Timestamp.now()
                )
                
                val success = firebaseHelper.updateUserProfile(currentUserId, updates)
                
                if (success) {
                    Log.d("ProfileActivity", "User status updated successfully")
                    // Update UI
                    findViewById<TextView>(R.id.profile_status)?.text = newStatus
                    Toast.makeText(this@ProfileActivity, "Status updated!", Toast.LENGTH_SHORT).show()
                } else {
                    Log.e("ProfileActivity", "Failed to update user status")
                }
            } catch (e: Exception) {
                Log.e("ProfileActivity", "Error updating status: ${e.message}")
            }
        }
    }
}
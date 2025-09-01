package com.example.stegochat

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ChangepasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.changepassword)

        // Initialize views
        val backArrow = findViewById<ImageView>(R.id.back_arrow)
        val currentPassword = findViewById<EditText>(R.id.current_password)
        val newPassword = findViewById<EditText>(R.id.new_password)
        val confirmPassword = findViewById<EditText>(R.id.confirm_password)
        val saveButton = findViewById<Button>(R.id.save_button)

        // Back Arrow Click
        backArrow.setOnClickListener {
            finish() // Go back to previous activity
        }

        // Save Button Click
        saveButton.setOnClickListener {
            val currentPwd = currentPassword.text.toString()
            val newPwd = newPassword.text.toString()
            val confirmPwd = confirmPassword.text.toString()

            if (validatePasswordChange(currentPwd, newPwd, confirmPwd)) {
                // TODO: Implement actual password change logic
                Toast.makeText(this, "Password changed successfully!", Toast.LENGTH_SHORT).show()
                
                // Navigate back to profile after successful password change
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun validatePasswordChange(currentPwd: String, newPwd: String, confirmPwd: String): Boolean {
        if (currentPwd.isEmpty()) {
            Toast.makeText(this, "Please enter current password", Toast.LENGTH_SHORT).show()
            return false
        }

        if (newPwd.isEmpty()) {
            Toast.makeText(this, "Please enter new password", Toast.LENGTH_SHORT).show()
            return false
        }

        if (confirmPwd.isEmpty()) {
            Toast.makeText(this, "Please confirm new password", Toast.LENGTH_SHORT).show()
            return false
        }

        if (newPwd != confirmPwd) {
            Toast.makeText(this, "New passwords do not match", Toast.LENGTH_SHORT).show()
            return false
        }

        if (newPwd.length < 8) {
            Toast.makeText(this, "Password must be at least 8 characters long", Toast.LENGTH_SHORT).show()
            return false
        }

        // TODO: Add more password validation (uppercase, lowercase, numbers, special characters)
        
        return true
    }
}
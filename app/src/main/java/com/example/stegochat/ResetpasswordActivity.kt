package com.example.stegochat

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ResetpasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.resetpassword)

        val newPassword = findViewById<EditText>(R.id.newPasswordEditText)
        val confirmPassword = findViewById<EditText>(R.id.confirmPasswordEditText)
        val continueButton = findViewById<Button>(R.id.continueButton)

        continueButton.setOnClickListener {
            val newPwd = newPassword.text.toString()
            val confirmPwd = confirmPassword.text.toString()

            if (!validateNewPassword(newPwd, confirmPwd)) return@setOnClickListener

            // TODO: Persist the new password (backend or local auth provider)
            Toast.makeText(this, "Password reset successful", Toast.LENGTH_SHORT).show()

            // Navigate back to Login
            val intent = Intent(this, Loginpage::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun validateNewPassword(newPwd: String, confirmPwd: String): Boolean {
        if (newPwd.isEmpty()) {
            Toast.makeText(this, "Please enter new password", Toast.LENGTH_SHORT).show()
            return false
        }
        if (confirmPwd.isEmpty()) {
            Toast.makeText(this, "Please confirm new password", Toast.LENGTH_SHORT).show()
            return false
        }
        if (newPwd != confirmPwd) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return false
        }
        if (newPwd.length < 8) {
            Toast.makeText(this, "Password must be at least 8 characters", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
}
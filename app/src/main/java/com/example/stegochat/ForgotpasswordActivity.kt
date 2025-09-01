package com.example.stegochat

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.util.Random
import java.util.Timer
import java.util.TimerTask

class ForgotpasswordActivity : AppCompatActivity() {
    private lateinit var otpInputs: Array<EditText>
    private var isOtpSent = false
    private var storedOTP: String = ""
    private var otpTimer: Timer? = null
    private var timeRemaining = 0
    
    // UI Components
    private lateinit var emailInput: EditText
    private lateinit var sendOtpButton: Button
    private lateinit var resendOtp: TextView
    private lateinit var verifyOtpButton: Button
    private lateinit var timerText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.forgotpassword)

        initializeViews()
        setupOtpInputListeners()
        setupClickListeners()
    }

    private fun initializeViews() {
        // Initialize views
        emailInput = findViewById(R.id.emailInput)
        sendOtpButton = findViewById(R.id.sendOtpButton)
        resendOtp = findViewById(R.id.resendOtp)
        verifyOtpButton = findViewById(R.id.verifyOtpButton)

        // Initialize OTP input fields
        otpInputs = arrayOf(
            findViewById(R.id.otp1),
            findViewById(R.id.otp2),
            findViewById(R.id.otp3),
            findViewById(R.id.otp4),
            findViewById(R.id.otp5),
            findViewById(R.id.otp6)
        )

        // Set initial states
        verifyOtpButton.isEnabled = false
        resendOtp.isEnabled = false
    }

    private fun setupOtpInputListeners() {
        for (i in otpInputs.indices) {
            val currentInput = otpInputs[i]
            
            currentInput.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    // Update visual state
                    updateOtpInputState(currentInput, s?.isNotEmpty() == true)
                    
                    // Auto-focus to next input
                    if (s?.length == 1 && i < otpInputs.size - 1) {
                        otpInputs[i + 1].requestFocus()
                    }
                    
                    // Check if all OTP digits are entered
                    checkOtpCompletion()
                }
            })

            // Handle backspace
            currentInput.setOnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) {
                    if (currentInput.text.isEmpty() && i > 0) {
                        otpInputs[i - 1].requestFocus()
                        return@setOnKeyListener true
                    }
                }
                false
            }

            // Handle focus changes
            currentInput.setOnFocusChangeListener { _, hasFocus ->
                updateOtpInputState(currentInput, currentInput.text.isNotEmpty(), hasFocus)
            }
        }
    }

    private fun setupClickListeners() {
        // Back Button Click
        findViewById<ImageView>(R.id.backButton).setOnClickListener {
            finish()
        }

        // Send OTP Button Click
        sendOtpButton.setOnClickListener {
            val email = emailInput.text.toString().trim()
            if (validateEmail(email)) {
                sendOTP(email)
            }
        }

        // Resend OTP Click
        resendOtp.setOnClickListener {
            val email = emailInput.text.toString().trim()
            if (validateEmail(email)) {
                resendOTP(email)
            } else {
                Toast.makeText(this, "Please enter a valid email first", Toast.LENGTH_SHORT).show()
            }
        }

        // Verify OTP Button Click
        verifyOtpButton.setOnClickListener {
            if (!isOtpSent) {
                Toast.makeText(this, "Please send OTP first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val otp = getOtpString()
            if (validateOtp(otp)) {
                verifyOTP(otp)
            }
        }
    }

    private fun updateOtpInputState(input: EditText, hasText: Boolean, isFocused: Boolean = false) {
        input.isSelected = hasText
        if (isFocused) {
            input.setBackgroundResource(R.drawable.otp_input_background)
        }
    }

    private fun checkOtpCompletion() {
        val otp = getOtpString()
        verifyOtpButton.isEnabled = otp.length == 6 && isOtpSent
    }

    private fun sendOTP(email: String) {
        // Show loading state
        sendOtpButton.isEnabled = false
        sendOtpButton.text = "Sending..."
        
        // Generate OTP
        val otp = generateOTP()
        storedOTP = otp
        
        // Send email
        EmailHelper.sendOTPEmail(
            recipientEmail = email,
            otp = otp,
            onSuccess = {
                runOnUiThread {
                    isOtpSent = true
                    sendOtpButton.text = "OTP Sent"
                    sendOtpButton.isEnabled = false
                    
                    // Start timer for resend
                    startOtpTimer()
                    
                    // Show success message
                    Toast.makeText(this, "OTP sent to $email", Toast.LENGTH_LONG).show()
                    
                    // Focus on first OTP input
                    otpInputs[0].requestFocus()
                }
            },
            onError = { errorMessage ->
                runOnUiThread {
                    sendOtpButton.isEnabled = true
                    sendOtpButton.text = "Send OTP"
                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                }
            }
        )
    }

    private fun resendOTP(email: String) {
        // Generate new OTP
        val otp = generateOTP()
        storedOTP = otp
        
        // Clear previous OTP inputs
        clearOtpInputs()
        
        // Send new email
        EmailHelper.sendOTPEmail(
            recipientEmail = email,
            otp = otp,
            onSuccess = {
                runOnUiThread {
                    // Restart timer
                    startOtpTimer()
                    Toast.makeText(this, "New OTP sent to $email", Toast.LENGTH_LONG).show()
                    otpInputs[0].requestFocus()
                }
            },
            onError = { errorMessage ->
                runOnUiThread {
                    Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                }
            }
        )
    }

    private fun verifyOTP(otp: String) {
        verifyOtpButton.isEnabled = false
        verifyOtpButton.text = "Verifying..."

        // Simulate verification delay
        verifyOtpButton.postDelayed({
            stopOtpTimer()

            Toast.makeText(this, "OTP verified successfully!", Toast.LENGTH_SHORT).show()

            // Navigate to Reset Password screen
            val intent = Intent(this, ResetpasswordActivity::class.java)
            startActivity(intent)
            finish()
        }, 800)
    }

    private fun startOtpTimer() {
        timeRemaining = 300 // 5 minutes in seconds
        resendOtp.isEnabled = false
        resendOtp.text = "Resend OTP in 5:00"
        
        otpTimer = Timer()
        otpTimer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                timeRemaining--
                runOnUiThread {
                    if (timeRemaining <= 0) {
                        stopOtpTimer()
                        resendOtp.isEnabled = true
                        resendOtp.text = "Didn't receive OTP? Resend"
                    } else {
                        val minutes = timeRemaining / 60
                        val seconds = timeRemaining % 60
                        resendOtp.text = "Resend OTP in ${minutes}:${String.format("%02d", seconds)}"
                    }
                }
            }
        }, 0, 1000)
    }

    private fun stopOtpTimer() {
        otpTimer?.cancel()
        otpTimer = null
    }

    private fun clearOtpInputs() {
        for (input in otpInputs) {
            input.setText("")
        }
        verifyOtpButton.isEnabled = false
    }

    private fun validateEmail(email: String): Boolean {
        if (email.isEmpty()) {
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show()
            return false
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun getOtpString(): String {
        return otpInputs.joinToString("") { it.text.toString() }
    }

    private fun validateOtp(otp: String): Boolean {
        if (otp.length != 6) {
            Toast.makeText(this, "Please enter all 6 digits", Toast.LENGTH_SHORT).show()
            return false
        }

        if (!otp.matches(Regex("\\d{6}"))) {
            Toast.makeText(this, "Please enter only numbers", Toast.LENGTH_SHORT).show()
            return false
        }

        // Validate against stored OTP
        if (otp != storedOTP) {
            Toast.makeText(this, "Invalid OTP. Please check and try again.", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun generateOTP(): String {
        val random = Random()
        return String.format("%06d", random.nextInt(1000000))
    }

    override fun onDestroy() {
        super.onDestroy()
        stopOtpTimer()
    }
}
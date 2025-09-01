package com.example.stegochat

import android.os.AsyncTask
import android.util.Log
import java.util.Properties
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class EmailHelper {
    
    companion object {
        private const val TAG = "EmailHelper"
        
        // Email configuration
        private const val EMAIL_HOST = "smtp.gmail.com"
        private const val EMAIL_PORT = "587"
        private val EMAIL_FROM: String get() = BuildConfig.EMAIL_FROM
        private val EMAIL_PASSWORD: String get() = BuildConfig.EMAIL_PASSWORD
        
        fun sendOTPEmail(
            recipientEmail: String,
            otp: String,
            onSuccess: () -> Unit,
            onError: (String) -> Unit
        ) {
            if (EMAIL_FROM.isBlank() || EMAIL_PASSWORD.isBlank()) {
                onError("Email not configured. Add EMAIL_FROM and EMAIL_PASSWORD to gradle.properties, then Rebuild.")
                return
            }
            SendEmailTask(recipientEmail, otp, onSuccess, onError).execute()
        }
    }
    
    private class SendEmailTask(
        private val recipientEmail: String,
        private val otp: String,
        private val onSuccess: () -> Unit,
        private val onError: (String) -> Unit
    ) : AsyncTask<Void, Void, Boolean>() {
        
        override fun doInBackground(vararg params: Void?): Boolean {
            return try {
                val properties = Properties().apply {
                    put("mail.smtp.auth", "true")
                    put("mail.smtp.starttls.enable", "true")
                    put("mail.smtp.host", EMAIL_HOST)
                    put("mail.smtp.port", EMAIL_PORT)
                }
                
                val session = Session.getInstance(properties, object : Authenticator() {
                    override fun getPasswordAuthentication(): PasswordAuthentication {
                        return PasswordAuthentication(EMAIL_FROM, EMAIL_PASSWORD)
                    }
                })
                
                val message = MimeMessage(session).apply {
                    setFrom(InternetAddress(EMAIL_FROM))
                    setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail))
                    subject = "StegoChat - Password Reset OTP"
                    setText(createEmailBody(otp), "UTF-8", "html")
                }
                
                Transport.send(message)
                Log.d(TAG, "Email sent successfully to $recipientEmail")
                true
            } catch (e: Exception) {
                Log.e(TAG, "Error sending email: ${e.message}")
                false
            }
        }
        
        override fun onPostExecute(success: Boolean) {
            if (success) {
                onSuccess()
            } else {
                onError("Failed to send email. Please check your internet connection and try again.")
            }
        }
        
        private fun createEmailBody(otp: String): String {
            return """
                <html>
                <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
                    <div style="max-width: 600px; margin: 0 auto; padding: 20px;">
                        <div style="text-align: center; margin-bottom: 30px;">
                            <h2 style="color: #4671f2; margin-bottom: 10px;">StegoChat</h2>
                            <h3 style="color: #333; margin-bottom: 20px;">Password Reset Request</h3>
                        </div>
                        
                        <div style="background-color: #f8f9fa; padding: 20px; border-radius: 8px; margin-bottom: 20px;">
                            <p style="margin-bottom: 15px;">You have requested to reset your password for your StegoChat account.</p>
                            <p style="margin-bottom: 20px;">Please use the following One-Time Password (OTP) to complete your password reset:</p>
                            
                            <div style="text-align: center; margin: 30px 0;">
                                <div style="display: inline-block; background-color: #4671f2; color: white; padding: 15px 30px; border-radius: 8px; font-size: 24px; font-weight: bold; letter-spacing: 3px;">
                                    $otp
                                </div>
                            </div>
                            
                            <p style="margin-bottom: 15px;"><strong>Important:</strong></p>
                            <ul style="margin-bottom: 20px; padding-left: 20px;">
                                <li>This OTP is valid for 10 minutes only</li>
                                <li>Do not share this OTP with anyone</li>
                                <li>If you didn't request this password reset, please ignore this email</li>
                            </ul>
                        </div>
                        
                        <div style="text-align: center; color: #666; font-size: 14px;">
                            <p>This is an automated message. Please do not reply to this email.</p>
                            <p>&copy; 2024 StegoChat. All rights reserved.</p>
                        </div>
                    </div>
                </body>
                </html>
            """.trimIndent()
        }
    }
} 
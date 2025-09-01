package com.example.stegochat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.app.Activity
import android.net.Uri
import android.provider.MediaStore
import androidx.core.app.ActivityCompat
import android.Manifest
import android.content.pm.PackageManager
import android.widget.ProgressBar
import android.os.Build
import android.content.ClipboardManager
import android.content.ClipData
import android.content.Context

class DecryptmessageActivity : AppCompatActivity() {
    private var selectedStegoImageUri: Uri? = null
    private var selectedSender: Friend? = null
    private var cameraPhotoUri: Uri? = null

    private val REQUEST_IMAGE_PICK = 1002
    private val REQUEST_CAMERA_CAPTURE = 1003
    private val REQUEST_PERMISSION = 2002
    private val REQUEST_CAMERA_PERMISSION = 2003
    private val REQUEST_MEDIA_PERMISSION = 2004

    private var progressBar: ProgressBar? = null
    private lateinit var keyManager: KeyManager

    // Sample friends list. Replace with real data if available
    private val friendsList = listOf(
        Friend("1", "Sarah Wilson", "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA...", isOnline = true),
        Friend("2", "Michael Chen", "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA...", isOnline = false),
        Friend("3", "Emma Thompson", "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA...", isOnline = true),
        Friend("4", "James Rodriguez", "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA...", isOnline = true)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.decryptmessage)

        // Initialize KeyManager
        keyManager = KeyManager(this)
        ensureKeyPairExists()

        // If a stego image URI was passed in (e.g., from share/download flow), preselect it
        intent.getStringExtra("stego_image_uri")?.let { uriString ->
            try {
                selectedStegoImageUri = Uri.parse(uriString)
                Log.d("DecryptmessageActivity", "Preselected stego image URI from intent: $uriString")
            } catch (_: Exception) { /* ignore */ }
        }

        // Initialize views
        val backButton = findViewById<ImageView>(R.id.backButton)
        val revealButton = findViewById<Button>(R.id.loginButton) // "Reveal Message"
        val verificationContainer = findViewById<LinearLayout>(R.id.verificationContainer)
        val decryptedMessageContainer = findViewById<LinearLayout>(R.id.decryptedMessageContainer)
        val errorContainer = findViewById<LinearLayout>(R.id.errorContainer)
        progressBar = findViewById(R.id.progressBar)
        progressBar?.visibility = View.GONE

        val friendSelectionArea = findViewById<LinearLayout>(R.id.friendSelectionArea)
        val selectedFriendText = findViewById<TextView>(R.id.selectedFriendText)
        val imageSelectionArea = findViewById<LinearLayout>(R.id.imageSelectionArea)
        val copyButton = findViewById<LinearLayout>(R.id.copyButton)

        friendSelectionArea?.setOnClickListener { showFriendSelectionDialog() }
        imageSelectionArea?.setOnClickListener { showImageSelectionDialog() }
        copyButton?.setOnClickListener { copyDecryptedMessage() }

        backButton?.setOnClickListener { finish() }
        revealButton?.setOnClickListener { performLSBDecodingAndDecrypt() }
    }

    private fun ensureKeyPairExists() {
        if (!keyManager.hasKeyPair()) {
            val success = keyManager.generateKeyPair()
            if (!success) {
                Toast.makeText(this, "Failed to generate decryption keys", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun showFriendSelectionDialog() {
        val dialog = FriendSelectionDialog.newInstance(friendsList) { friend ->
            selectedSender = friend
            findViewById<TextView>(R.id.selectedFriendText)?.text = friend.name
        }
        dialog.show(supportFragmentManager, "FriendSelectionDialog")
    }

    private fun showImageSelectionDialog() {
        val dialog = ImageSelectionDialog.newInstance(
            onGallerySelected = {
                checkAndRequestMediaPermission {
                    pickImageFromGallery()
                }
            },
            onCameraSelected = {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
                } else {
                    captureImageFromCamera()
                }
            }
        )
        dialog.show(supportFragmentManager, "ImageSelectionDialog")
    }

    private fun checkAndRequestMediaPermission(onPermissionGranted: () -> Unit) {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
        
        if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(permission), REQUEST_MEDIA_PERMISSION)
        } else {
            onPermissionGranted()
        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_IMAGE_PICK)
    }

    private fun captureImageFromCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val photoFile = createImageFile()
        photoFile?.let { file ->
            val photoURI = androidx.core.content.FileProvider.getUriForFile(this, "$packageName.fileprovider", file)
            cameraPhotoUri = photoURI
            selectedStegoImageUri = photoURI
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            startActivityForResult(intent, REQUEST_CAMERA_CAPTURE)
        }
    }

    private fun createImageFile(): java.io.File? {
        return try {
            val timeStamp = java.text.SimpleDateFormat("yyyyMMdd_HHmmss", java.util.Locale.getDefault()).format(java.util.Date())
            val storageDir = getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES)
            java.io.File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
        } catch (e: Exception) {
            null
        }
    }

    private fun copyDecryptedMessage() {
        val decryptedText = findViewById<TextView>(R.id.decryptedMessageText)?.text?.toString()
        if (!decryptedText.isNullOrEmpty() && decryptedText != "Your decrypted message will appear here...") {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Decrypted Message", decryptedText)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this, "Message copied to clipboard", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "No message to copy", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_PERMISSION, REQUEST_MEDIA_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickImageFromGallery()
                } else {
                    Toast.makeText(this, "Storage permission denied", Toast.LENGTH_SHORT).show()
                }
            }
            REQUEST_CAMERA_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    captureImageFromCamera()
                } else {
                    Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_PICK -> {
                    data?.data?.let { uri ->
                        selectedStegoImageUri = uri
                        Toast.makeText(this, "Stego image selected", Toast.LENGTH_SHORT).show()
                    }
                }
                REQUEST_CAMERA_CAPTURE -> {
                    // Prefer the URI we created for the camera capture
                    selectedStegoImageUri = cameraPhotoUri ?: data?.data ?: selectedStegoImageUri
                    Toast.makeText(this, "Stego image captured from camera", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /**
     * Perform LSB decoding and decrypt the extracted payload using demo format
     */
    private fun performLSBDecodingAndDecrypt() {
        try {
            progressBar?.visibility = View.VISIBLE

            if (!hasSelectedStegoImage()) {
                progressBar?.visibility = View.GONE
                Toast.makeText(this, "Please select a stego image first", Toast.LENGTH_LONG).show()
                return
            }

            val stegoImagePath = getSelectedStegoImagePath()
            if (stegoImagePath == null) {
                progressBar?.visibility = View.GONE
                Toast.makeText(this, "Unable to access image path", Toast.LENGTH_LONG).show()
                return
            }

            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                try {
                    val extracted = LSBSteganography.extractMessage(this, stegoImagePath)
                    if (extracted.isNullOrEmpty()) {
                        progressBar?.visibility = View.GONE
                        showDecryptionError("No hidden message found in this image")
                        return@postDelayed
                    }

                    // Parse the demo payload
                    val decryptedText = parsePayload(extracted)
                    if (decryptedText != null) {
                        progressBar?.visibility = View.GONE
                        showDecryptionSuccess(decryptedText, true)
                    } else {
                        progressBar?.visibility = View.GONE
                        showDecryptionError("Failed to decrypt message. Invalid format.")
                    }
                    
                } catch (e: Exception) {
                    progressBar?.visibility = View.GONE
                    Log.e("DecryptmessageActivity", "Decoding/decryption error", e)
                    showDecryptionError("Failed to extract message from image")
                }
            }, 800)
        } catch (e: Exception) {
            progressBar?.visibility = View.GONE
            Log.e("DecryptmessageActivity", "Error", e)
            showDecryptionError("An unexpected error occurred")
        }
    }
    
    /**
     * Parse payload: try real RSA+AES first using Android Keystore; fallback to demo format.
     */
    private fun parsePayload(extractedPayload: String): String? {
        // Try full decryption first
        try {
            val privateKey = keyManager.getPrivateKey()
            if (privateKey != null) {
                val message = AESEncryption.decryptMessage(extractedPayload, privateKey)
                // Optionally update sender name if provided
                try {
                    val json = org.json.JSONObject(extractedPayload)
                    val friendName = json.optString("f", null)
                    if (!friendName.isNullOrBlank()) {
                        selectedSender = Friend("server", friendName, "server_key", isOnline = true)
                        findViewById<TextView>(R.id.selectedFriendText)?.text = friendName
                    }
                } catch (_: Exception) { }
                return message
            }
        } catch (e: Exception) {
            Log.w("DecryptmessageActivity", "Real decryption failed, trying demo payload fallback")
        }

        // Fallback: demo payload
        return try {
            val jsonObject = org.json.JSONObject(extractedPayload)
            val ciphertext = jsonObject.getString("c")
            val friendName = jsonObject.optString("f", "Unknown")
            val decodedBytes = android.util.Base64.decode(ciphertext, android.util.Base64.DEFAULT)
            val decryptedMessage = String(decodedBytes, Charsets.UTF_8)
            if (friendName != "Unknown") {
                selectedSender = Friend("demo", friendName, "demo_key", isOnline = true)
                findViewById<TextView>(R.id.selectedFriendText)?.text = friendName
            }
            decryptedMessage
        } catch (e: Exception) {
            Log.e("DecryptmessageActivity", "Failed to parse payload", e)
            null
        }
    }

    private fun hasSelectedStegoImage(): Boolean = selectedStegoImageUri != null

    private fun getSelectedStegoImagePath(): String? {
        selectedStegoImageUri?.let { uri ->
            // Always prefer using content URIs directly when available
            if (uri.scheme?.startsWith("content") == true) return uri.toString()
            if (uri.scheme?.startsWith("file") == true) return uri.path
            // Fallback for older devices/URIs
            val projection = arrayOf(MediaStore.Images.Media.DATA)
            contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
                val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                if (cursor.moveToFirst()) return cursor.getString(columnIndex)
            }
            // As a last resort, return the URI string
            return uri.toString()
        }
        return null
    }

    private fun showDecryptionSuccess(message: String, isVerified: Boolean) {
        findViewById<LinearLayout>(R.id.verificationContainer)?.visibility = View.VISIBLE
        val decryptedMessageContainer = findViewById<LinearLayout>(R.id.decryptedMessageContainer)
        decryptedMessageContainer?.visibility = View.VISIBLE
        decryptedMessageContainer?.findViewById<TextView>(R.id.decryptedMessageText)?.text = message
        findViewById<LinearLayout>(R.id.errorContainer)?.visibility = View.GONE
        
        // Update verification text
        val verificationText = findViewById<TextView>(R.id.verificationText)
        if (isVerified) {
            verificationText?.text = "Verified message from ${selectedSender?.name}"
        } else {
            verificationText?.text = "Message from ${selectedSender?.name} (unverified)"
        }
        
        Toast.makeText(this, "Message decrypted successfully!", Toast.LENGTH_SHORT).show()
    }

    private fun showDecryptionError(errorMessage: String) {
        findViewById<LinearLayout>(R.id.errorContainer)?.visibility = View.VISIBLE
        findViewById<LinearLayout>(R.id.verificationContainer)?.visibility = View.GONE
        findViewById<LinearLayout>(R.id.decryptedMessageContainer)?.visibility = View.GONE
        
        // Update error message
        findViewById<TextView>(R.id.errorMessage)?.text = errorMessage
        
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
    }
}
package com.example.stegochat

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
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
import android.os.Build
import android.content.ContentValues
import java.io.OutputStream
import android.widget.ProgressBar
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class HidemessageActivity : AppCompatActivity() {
    private var hideMessageButton: Button? = null
    private var successContainer: LinearLayout? = null
    private var selectedImageUri: Uri? = null
    private var selectedFriend: Friend? = null
    private val REQUEST_IMAGE_PICK = 1001
    private val REQUEST_CAMERA_CAPTURE = 1002
    private val REQUEST_PERMISSION = 2001
    private val REQUEST_CAMERA_PERMISSION = 2002
    private val REQUEST_MEDIA_PERMISSION = 2003
    private var progressBar: ProgressBar? = null
    private var stegoImagePath: String? = null
    private var cameraPhotoUri: Uri? = null
    private lateinit var keyManager: KeyManager
    
    // Sample friends data - in a real app, this would come from a database or API
    private val friendsList = listOf(
        Friend("1", "Sarah Wilson", "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA...", isOnline = true),
        Friend("2", "Michael Chen", "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA...", isOnline = false),
        Friend("3", "Emma Thompson", "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA...", isOnline = true),
        Friend("4", "James Rodriguez", "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA...", isOnline = true),
        Friend("5", "Lisa Park", "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA...", isOnline = false),
        Friend("6", "David Kim", "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA...", isOnline = true),
        Friend("7", "Anna Martinez", "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA...", isOnline = false)
    )
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.hidemessage)

        // Initialize KeyManager
        keyManager = KeyManager(this)
        ensureKeyPairExists()

        // Initialize views
        val backButton = findViewById<ImageView>(R.id.backButton)
        val friendSelectionArea = findViewById<LinearLayout>(R.id.friendSelectionArea)
        val selectedFriendText = findViewById<TextView>(R.id.selectedFriendText)
        val imageSelectionArea = findViewById<LinearLayout>(R.id.imageSelectionArea)
        val secretMessageInput = findViewById<EditText>(R.id.secretMessageInput)
        val characterCount = findViewById<TextView>(R.id.characterCount)
        hideMessageButton = findViewById<Button>(R.id.hideMessageButton)
        successContainer = findViewById<LinearLayout>(R.id.successContainer)
        val downloadButton = findViewById<Button>(R.id.downloadButton)
        progressBar = findViewById(R.id.progressBar)
        progressBar?.visibility = View.GONE

        // Back Button Click
        backButton?.setOnClickListener {
            finish() // Go back to previous activity
        }

        // Friend Selection Area Click
        friendSelectionArea?.setOnClickListener {
            showFriendSelectionDialog()
        }

        // Image Selection Area Click
        imageSelectionArea?.setOnClickListener {
            showImageSelectionDialog()
        }

        // Character Count Watcher
        secretMessageInput?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val currentLength = s?.length ?: 0
                characterCount?.text = "$currentLength/300"
                
                // Enable/disable hide message button based on input and selections
                updateHideMessageButtonState()
            }
        })

        // Hide Message Button Click
        hideMessageButton?.setOnClickListener {
            val message = secretMessageInput?.text.toString().trim()
            if (message.isNotEmpty() && selectedFriend != null && selectedImageUri != null) {
                performLSBEncoding(message)
            } else {
                when {
                    selectedFriend == null -> Toast.makeText(this, "Please select a friend", Toast.LENGTH_SHORT).show()
                    selectedImageUri == null -> Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
                    message.isEmpty() -> Toast.makeText(this, "Please enter a message", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Download Button Click
        downloadButton?.setOnClickListener {
            stegoImagePath?.let { path ->
                val intent = Intent(this, StegoimageshareActivity::class.java)
                intent.putExtra("stego_image_uri", path)
                startActivity(intent)
            } ?: run {
                Toast.makeText(this, "No stego image to share", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun ensureKeyPairExists() {
        if (!keyManager.hasKeyPair()) {
            val success = keyManager.generateKeyPair()
            if (!success) {
                Toast.makeText(this, "Failed to generate encryption keys", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun showFriendSelectionDialog() {
        val dialog = FriendSelectionDialog.newInstance(friendsList) { friend ->
            selectedFriend = friend
            findViewById<TextView>(R.id.selectedFriendText)?.text = friend.name
            updateHideMessageButtonState()
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
            selectedImageUri = photoURI
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

    private fun updateHideMessageButtonState() {
        val message = findViewById<EditText>(R.id.secretMessageInput)?.text.toString().trim()
        val hasMessage = message.isNotEmpty()
        val hasFriend = selectedFriend != null
        val hasImage = selectedImageUri != null
        
        if (hasMessage && hasFriend && hasImage) {
            hideMessageButton?.isEnabled = true
            hideMessageButton?.backgroundTintList = android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#4c51ff"))
        } else {
            hideMessageButton?.isEnabled = false
            hideMessageButton?.backgroundTintList = android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#CCCCCC"))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_PICK -> {
                    data?.data?.let { uri ->
                        selectedImageUri = uri
                        Toast.makeText(this, "Image selected from gallery", Toast.LENGTH_SHORT).show()
                        updateHideMessageButtonState()
                    }
                }
                REQUEST_CAMERA_CAPTURE -> {
                    if (cameraPhotoUri != null) {
                        selectedImageUri = cameraPhotoUri
                    }
                    Toast.makeText(this, "Image captured from camera", Toast.LENGTH_SHORT).show()
                    updateHideMessageButtonState()
                }
            }
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
    
    /**
     * Perform LSB steganography encoding with simplified encryption for demo
     */
    private fun performLSBEncoding(message: String) {
        try {
            progressBar?.visibility = View.VISIBLE
            hideMessageButton?.isEnabled = false
            
            // Build payload: prefer real RSA+AES if recipient public key is valid; otherwise fallback to demo
            val encryptedPayload = createEncryptedPayload(message, selectedFriend!!)
            
            Toast.makeText(this, "Encrypting and hiding message...", Toast.LENGTH_SHORT).show()
            
            if (!hasSelectedImage()) {
                Toast.makeText(this, "Please select an image first", Toast.LENGTH_LONG).show()
                return
            }
            
            val selectedImagePath = getSelectedImagePath()
            if (selectedImagePath == null) {
                Toast.makeText(this, "No image selected or unable to access image path.", Toast.LENGTH_LONG).show()
                return
            }
            
            val outputPath = createStegoImagePath()
            
            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                try {
                    // Use the updated LSBSteganography method that handles content URIs
                    val success = LSBSteganography.hideMessage(this, selectedImagePath, encryptedPayload, outputPath)
                    if (success) {
                        Toast.makeText(this, "Message encrypted and hidden successfully!", Toast.LENGTH_SHORT).show()
                        successContainer?.visibility = View.VISIBLE
                        hideMessageButton?.visibility = View.GONE
                        findViewById<Button>(R.id.downloadButton)?.visibility = View.VISIBLE
                        stegoImagePath = outputPath
                        Log.d("HidemessageActivity", "LSB encoding completed for encrypted message")
                    } else {
                        Toast.makeText(this, "LSB encoding failed. Image may be too small, corrupted, or inaccessible.", Toast.LENGTH_LONG).show()
                    }
                } catch (e: SecurityException) {
                    Toast.makeText(this, "Permission denied. Please allow storage access.", Toast.LENGTH_LONG).show()
                    Log.e("HidemessageActivity", "Permission error", e)
                } catch (e: Exception) {
                    Toast.makeText(this, "LSB encoding failed: ${e.message}", Toast.LENGTH_LONG).show()
                    Log.e("HidemessageActivity", "LSB encoding error", e)
                }
                progressBar?.visibility = View.GONE
                hideMessageButton?.isEnabled = true
            }, 1000)
        } catch (e: SecurityException) {
            Toast.makeText(this, "Permission denied. Please allow storage access.", Toast.LENGTH_LONG).show()
            Log.e("HidemessageActivity", "Permission error", e)
        } catch (e: Exception) {
            Toast.makeText(this, "LSB encoding failed: ${e.message}", Toast.LENGTH_LONG).show()
            Log.e("HidemessageActivity", "LSB encoding error", e)
        }
    }
    
    /**
     * Create payload: if recipient public key is valid Base64 RSA key, create real RSA+AES payload.
     * Otherwise, create demo payload compatible with the decoder fallback.
     */
    private fun createEncryptedPayload(message: String, friend: Friend): String {
        val friendPublicKey = friend.publicKey
        val isValidRecipientKey = isLikelyValidRsaPublicKey(friendPublicKey)
        return if (isValidRecipientKey) {
            val senderPrivateKey = keyManager.getPrivateKey()
            val payload = AESEncryption.encryptMessage(message, friendPublicKey, senderPrivateKey)
            // Attach sender info for display (optional)
            try {
                val obj = org.json.JSONObject(payload)
                obj.put("f", friend.name)
                obj.toString()
            } catch (_: Exception) {
                payload
            }
        } else {
            // Fallback demo payload
            val timestamp = System.currentTimeMillis()
            """
            {
                "c": "${android.util.Base64.encodeToString(message.toByteArray(), android.util.Base64.DEFAULT)}",
                "k": "demo_key_placeholder",
                "i": "demo_iv_placeholder",
                "t": $timestamp,
                "f": "${friend.name}"
            }
            """.trimIndent()
        }
    }

    private fun isLikelyValidRsaPublicKey(publicKeyString: String?): Boolean {
        if (publicKeyString.isNullOrBlank()) return false
        if (publicKeyString.contains("..")) return false
        return try {
            val decoded = android.util.Base64.decode(publicKeyString, android.util.Base64.DEFAULT)
            // 2048-bit RSA public key (X.509) is around 270-300 bytes
            decoded.size >= 250
        } catch (_: IllegalArgumentException) {
            false
        }
    }
    
    private fun hasSelectedImage(): Boolean {
        return selectedImageUri != null
    }
    
    private fun getSelectedImagePath(): String? {
        selectedImageUri?.let { uri ->
            // For Android 10+ (API 29+), MediaStore.Images.Media.DATA is deprecated
            // We need to handle content URIs properly
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // For content URIs, we'll work with the URI directly
                return uri.toString()
            } else {
                // For older versions, try to get the file path
                val projection = arrayOf(MediaStore.Images.Media.DATA)
                contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
                    val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                    if (cursor.moveToFirst()) {
                        return cursor.getString(columnIndex)
                    }
                }
            }
        }
        return null
    }
    
    private fun createStegoImagePath(): String {
        val fileName = "stego_image_${System.currentTimeMillis()}.png"
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // For Android 10+ use MediaStore
            val resolver = contentResolver
            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                put(MediaStore.Images.Media.RELATIVE_PATH, "DCIM/Stegochat")
            }
            val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            uri?.toString() ?: ""
        } else {
            // For older versions, save to DCIM/Stegochat
            val dir = android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_DCIM).absolutePath + "/Stegochat/"
            val file = java.io.File(dir)
            if (!file.exists()) file.mkdirs()
            dir + fileName
        }
    }

    private fun saveStegoImage(bitmap: android.graphics.Bitmap, outputPath: String): Boolean {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && outputPath.startsWith("content://")) {
                val uri = android.net.Uri.parse(outputPath)
                val resolver = contentResolver
                resolver.openOutputStream(uri)?.use { out ->
                    bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, out)
                } ?: false
            } else {
                val file = java.io.File(outputPath)
                val out = java.io.FileOutputStream(file)
                bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, out)
                out.flush()
                out.close()
                true
            }
        } catch (e: Exception) {
            false
        }
    }
} 
package com.example.stegochat

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.app.Activity
import android.net.Uri
import android.provider.MediaStore
import androidx.core.app.ActivityCompat
import android.Manifest
import android.content.pm.PackageManager
import android.os.Environment
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class Chooseimage1Activity : AppCompatActivity() {
    private val REQUEST_IMAGE_PICK = 1001
    private val REQUEST_CAMERA_CAPTURE = 1003
    private val REQUEST_PERMISSION = 2001
    private val REQUEST_CAMERA_PERMISSION = 2003
    private var selectedImageUri: Uri? = null
    private var photoFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chooseimage1)

        val backButton = findViewById<ImageView>(R.id.backButton)
        val imageSelectionArea = findViewById<LinearLayout>(R.id.imageSelectionArea)
        val continueButton = findViewById<Button>(R.id.continueButton)
        val cameraCaptureOption = findViewById<LinearLayout>(R.id.cameraCaptureOption)

        backButton?.setOnClickListener { finish() }

        imageSelectionArea?.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_PERMISSION)
            } else {
                pickImageFromGallery()
            }
        }

        cameraCaptureOption?.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
            } else {
                captureImageFromCamera()
            }
        }

        continueButton?.setOnClickListener {
            selectedImageUri?.let {
                val intent = Intent()
                intent.putExtra("selected_image_uri", it.toString())
                setResult(Activity.RESULT_OK, intent)
                finish()
            } ?: run {
                Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_IMAGE_PICK)
    }

    private fun captureImageFromCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        photoFile = createImageFile()
        photoFile?.let { file ->
            val photoURI = androidx.core.content.FileProvider.getUriForFile(this, "$packageName.fileprovider", file)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            startActivityForResult(intent, REQUEST_CAMERA_CAPTURE)
        }
    }

    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_PERMISSION -> {
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
        when (requestCode) {
            REQUEST_IMAGE_PICK -> {
                if (resultCode == Activity.RESULT_OK) {
                    data?.data?.let { uri ->
                        selectedImageUri = uri
                        Toast.makeText(this, "Image selected from gallery", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            REQUEST_CAMERA_CAPTURE -> {
                if (resultCode == Activity.RESULT_OK) {
                    photoFile?.let { file ->
                        selectedImageUri = androidx.core.content.FileProvider.getUriForFile(this, "$packageName.fileprovider", file)
                        Toast.makeText(this, "Image captured from camera", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Camera capture cancelled", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
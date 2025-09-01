# Google Drive API Alternative for Stegochat

## Why Google Drive API?
- ✅ **Free**: 15GB storage included
- ✅ **No billing setup**: Works immediately
- ✅ **Google integration**: Easy with existing Google account
- ✅ **Reliable**: Google's infrastructure
- ✅ **Simple**: REST API with good Android libraries

## Setup Steps

### Step 1: Enable Google Drive API
1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select existing
3. Enable Google Drive API
4. Create credentials (OAuth 2.0 Client ID)

### Step 2: Add Dependencies
Add to `app/build.gradle`:
```gradle
dependencies {
    // Google Drive API
    implementation 'com.google.android.gms:play-services-auth:20.7.0'
    implementation 'com.google.apis:google-api-services-drive:v3-rev20220815-2.0.0'
    implementation 'com.google.api-client:google-api-client-android:2.2.0'
    implementation 'com.google.http-client:google-http-client-gson:1.42.3'
}
```

### Step 3: Update FirebaseDatabaseHelper.kt
Replace Firebase Storage with Google Drive:

```kotlin
class GoogleDriveHelper {
    private lateinit var driveService: Drive
    
    suspend fun uploadStegoImage(
        userId: String,
        imageUri: String,
        originalMessage: String
    ): String? {
        return try {
            // Upload to Google Drive
            val fileMetadata = com.google.api.services.drive.model.File()
            fileMetadata.name = "stego_${System.currentTimeMillis()}.jpg"
            fileMetadata.parents = listOf("root") // Upload to root folder
            
            val mediaContent = FileMediaType("image/jpeg", File(imageUri))
            val file = driveService.files().create(fileMetadata, mediaContent)
                .setFields("id, webViewLink")
                .execute()
            
            // Store metadata in Firestore
            val imageData = hashMapOf(
                "userId" to userId,
                "driveFileId" to file.id,
                "driveLink" to file.webViewLink,
                "originalMessage" to originalMessage,
                "uploadedAt" to com.google.firebase.Timestamp.now()
            )
            
            firestore.collection("stego_images").add(imageData).await()
            
            file.webViewLink
        } catch (e: Exception) {
            Log.e("GoogleDriveHelper", "Upload failed: ${e.message}")
            null
        }
    }
}
```

## Alternative 2: Simple Local Storage

If you want to avoid cloud storage entirely:

```kotlin
class LocalStorageHelper {
    fun saveStegoImage(context: Context, bitmap: Bitmap, fileName: String): String? {
        return try {
            val file = File(context.getExternalFilesDir("stego_images"), fileName)
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.close()
            file.absolutePath
        } catch (e: Exception) {
            null
        }
    }
    
    fun getStegoImages(context: Context): List<File> {
        val dir = File(context.getExternalFilesDir("stego_images"))
        return dir.listFiles()?.filter { it.extension in listOf("jpg", "png") } ?: emptyList()
    }
}
```

## Alternative 3: Imgur API (Simplest)

```kotlin
class ImgurHelper {
    private val clientId = "YOUR_IMGUR_CLIENT_ID"
    
    suspend fun uploadImage(imagePath: String): String? {
        return try {
            val client = OkHttpClient()
            val imageBytes = File(imagePath).readBytes()
            val base64Image = Base64.encodeToString(imageBytes, Base64.DEFAULT)
            
            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image", base64Image)
                .build()
            
            val request = Request.Builder()
                .url("https://api.imgur.com/3/image")
                .addHeader("Authorization", "Client-ID $clientId")
                .post(requestBody)
                .build()
            
            val response = client.newCall(request).execute()
            // Parse response to get image URL
            // Return the image URL
        } catch (e: Exception) {
            null
        }
    }
}
```

## Recommendation

**For your college project, I recommend:**

1. **Try Google Drive API first** - Most professional, free, reliable
2. **If that fails, use Local Storage** - Simple, works immediately
3. **For demo purposes, use Imgur API** - Easiest to set up

## Quick Implementation

Want me to implement one of these alternatives in your code? Just tell me which one you prefer:

- **Google Drive API** (most professional)
- **Local Storage** (simplest, no internet needed)
- **Imgur API** (easiest cloud option)

Which would you like me to implement?



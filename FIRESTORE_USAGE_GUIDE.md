# Firestore Database Usage Guide for StegoChat

## 🎉 Congratulations! Your Firestore Setup is Complete

Your Android app is now fully connected to Firebase Firestore database. Here's everything you need to know:

## 📋 What's Already Set Up

### ✅ Dependencies (in `app/build.gradle`)
- `firebase-firestore-ktx` - Firestore database
- `kotlinx-coroutines-play-services` - Async operations
- `firebase-auth-ktx` - Authentication
- `firebase-storage-ktx` - File storage
- `firebase-messaging-ktx` - Push notifications

### ✅ Firebase Configuration
- `google-services.json` - Properly configured
- Firebase project: `stegochat-f8f9b`
- All necessary permissions in `AndroidManifest.xml`

### ✅ Database Helper (`FirebaseDatabaseHelper.kt`)
Complete Firestore operations for:
- 👤 User Management
- 👥 Friend System  
- 💬 Chat System
- 🖼️ Stego Image Metadata
- 🔐 Authentication

## 🚀 How to Use Firestore in Your App

### 1. Basic Usage Pattern

```kotlin
// Initialize the helper
val firebaseHelper = FirebaseDatabaseHelper()

// Use coroutines for async operations
CoroutineScope(Dispatchers.Main).launch {
    // Your Firestore operations here
    val result = firebaseHelper.someOperation()
}
```

### 2. User Profile Operations

```kotlin
// Create user profile
val success = firebaseHelper.createUserProfile(
    userId = "user123",
    email = "user@example.com", 
    displayName = "John Doe"
)

// Get user profile
val profile = firebaseHelper.getUserProfile("user123")

// Update user profile
val updates = mapOf(
    "status" to "Hello World!",
    "lastSeen" to Timestamp.now()
)
firebaseHelper.updateUserProfile("user123", updates)
```

### 3. Friend System

```kotlin
// Send friend request
firebaseHelper.sendFriendRequest("user1", "user2")

// Accept friend request
firebaseHelper.acceptFriendRequest("requestId")

// Get pending requests
val requests = firebaseHelper.getPendingFriendRequests("userId")

// Get friends list
val friends = firebaseHelper.getUserFriends("userId")
```

### 4. Chat System

```kotlin
// Create or get conversation
val conversationId = firebaseHelper.getOrCreateConversation("user1", "user2")

// Send message
firebaseHelper.sendMessage(
    conversationId = conversationId,
    senderId = "user1",
    message = "Hello!"
)

// Get conversation messages
val messages = firebaseHelper.getConversationMessages(conversationId)

// Get user's conversations
val conversations = firebaseHelper.getUserConversations("userId")
```

### 5. Stego Image Metadata

```kotlin
// Save stego image metadata
firebaseHelper.saveStegoImageMetadata(
    userId = "user123",
    imagePath = "/path/to/image.jpg",
    originalMessage = "Hidden message",
    recipientId = "friend123"
)

// Get user's stego images
val images = firebaseHelper.getUserStegoImages("user123")
```

## 🧪 Testing Your Firestore Setup

### Option 1: Use FirebaseTestActivity
Navigate to `FirebaseTestActivity` to test all Firebase features:
- Authentication
- User Management
- Friend System
- Chat System

### Option 2: Use FirestoreExampleActivity (New!)
Navigate to `FirestoreExampleActivity` for hands-on testing:
- Create user profiles
- Send test messages
- Update user status
- Real-time feedback

## 📊 Firestore Collections Structure

Your database is organized into these collections:

```
/users/{userId}
├── userId: String
├── email: String
├── displayName: String
├── createdAt: Timestamp
├── lastSeen: Timestamp
├── isOnline: Boolean
├── profilePicture: String
└── status: String

/friends/{friendId}
├── userId1: String
├── userId2: String
└── createdAt: Timestamp

/friend_requests/{requestId}
├── fromUserId: String
├── toUserId: String
├── status: String
└── createdAt: Timestamp

/conversations/{conversationId}
├── participants: Array<String>
├── createdAt: Timestamp
├── lastMessage: String
└── lastMessageTime: Timestamp

/messages/{messageId}
├── conversationId: String
├── senderId: String
├── message: String
├── messageType: String
├── timestamp: Timestamp
└── isRead: Boolean

/stego_images/{imageId}
├── userId: String
├── localImagePath: String
├── originalMessage: String
├── recipientId: String
├── createdAt: Timestamp
└── imageType: String
```

## 🔧 Firebase Console Setup

1. **Enable Firestore Database:**
   - Go to [Firebase Console](https://console.firebase.google.com)
   - Select your project: `stegochat-f8f9b`
   - Go to Firestore Database
   - Click "Create Database"
   - Choose "Start in test mode" (for development)

2. **Set Up Security Rules:**
   ```javascript
   rules_version = '2';
   service cloud.firestore {
     match /databases/{database}/documents {
       // Allow authenticated users to read/write their own data
       match /users/{userId} {
         allow read, write: if request.auth != null && request.auth.uid == userId;
       }
       
       // Allow users to read other users' basic info
       match /users/{userId} {
         allow read: if request.auth != null;
       }
       
       // Friend requests
       match /friend_requests/{requestId} {
         allow read, write: if request.auth != null && 
           (resource.data.fromUserId == request.auth.uid || 
            resource.data.toUserId == request.auth.uid);
       }
       
       // Friends
       match /friends/{friendId} {
         allow read, write: if request.auth != null && 
           (resource.data.userId1 == request.auth.uid || 
            resource.data.userId2 == request.auth.uid);
       }
       
       // Conversations
       match /conversations/{conversationId} {
         allow read, write: if request.auth != null && 
           request.auth.uid in resource.data.participants;
       }
       
       // Messages
       match /messages/{messageId} {
         allow read, write: if request.auth != null;
       }
       
       // Stego images
       match /stego_images/{imageId} {
         allow read, write: if request.auth != null && 
           resource.data.userId == request.auth.uid;
       }
     }
   }
   ```

## 🚨 Important Notes

### 1. Authentication Required
Most Firestore operations require the user to be authenticated:
```kotlin
if (!firebaseHelper.isUserAuthenticated()) {
    // Redirect to login
    return
}
```

### 2. Use Coroutines
All Firestore operations are asynchronous. Always use coroutines:
```kotlin
CoroutineScope(Dispatchers.Main).launch {
    val result = firebaseHelper.someOperation()
    // Update UI with result
}
```

### 3. Error Handling
Always handle potential errors:
```kotlin
try {
    val result = firebaseHelper.someOperation()
    // Handle success
} catch (e: Exception) {
    // Handle error
    Log.e("TAG", "Error: ${e.message}")
}
```

### 4. Offline Support
Firestore automatically handles offline scenarios:
- Data is cached locally
- Changes sync when connection is restored
- No additional code needed

## 🎯 Next Steps

1. **Test the Setup:**
   - Run your app
   - Navigate to `FirestoreExampleActivity`
   - Test all operations

2. **Integrate with Your App:**
   - Use `FirebaseDatabaseHelper` in your existing activities
   - Replace any local storage with Firestore operations

3. **Add Real-time Listeners:**
   - For chat messages
   - For friend requests
   - For online status

4. **Set Up Security Rules:**
   - Configure proper access control
   - Test with different user scenarios

## 🆘 Troubleshooting

### Common Issues:

1. **"Permission denied" errors:**
   - Check Firebase Console security rules
   - Ensure user is authenticated

2. **"Network error" messages:**
   - Check internet connection
   - Verify Firebase project configuration

3. **"Document not found" errors:**
   - Check if document exists
   - Verify document ID format

### Debug Tips:
- Use `firebaseHelper.testFirebaseConnection()` to test connectivity
- Check Logcat for detailed error messages
- Use Firebase Console to inspect data

## 📞 Support

If you encounter any issues:
1. Check the Firebase Console for errors
2. Review the security rules
3. Test with the provided example activities
4. Check Logcat for detailed error messages

Your Firestore setup is now complete and ready to use! 🎉

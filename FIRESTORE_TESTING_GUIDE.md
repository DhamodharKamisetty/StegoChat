# Firestore Testing Guide - StegoChat App

## 🔍 **How to Check if Firestore is Working**

### **Quick Test (Recommended)**

1. **Run your app** on device/emulator
2. **Login** with your Firebase account
3. **From MainActivity, tap "⚡ Quick Firestore Test"**
4. **Check the results:**

#### **✅ Success Indicators:**
- Shows "✅ FIRESTORE WORKING!"
- Displays your email and profile info
- Shows "Firestore test completed!" toast

#### **❌ Failure Indicators:**
- Shows "❌ NOT AUTHENTICATED" - Need to login first
- Shows "❌ FIRESTORE ERROR" - Check setup below

### **Detailed Testing**

#### **Method 1: Use FirestoreExampleActivity**
1. **Tap "💾 Firestore Example"** from MainActivity
2. **Test each operation:**
   - ✅ **Create User Profile** - Should succeed
   - ✅ **Get User Profile** - Should show your data
   - ✅ **Update User Status** - Should succeed
   - ✅ **Send Test Message** - Should succeed

#### **Method 2: Check Firebase Console**
1. **Go to [Firebase Console](https://console.firebase.google.com)**
2. **Select project: `stegochat-f8f9b`**
3. **Click "Firestore Database"**
4. **Look for collections:**
   - `users` - Your profile data
   - `conversations` - Test conversations
   - `messages` - Test messages

#### **Method 3: Check Logcat**
1. **Open Android Studio**
2. **Go to Logcat tab**
3. **Filter by "FirebaseDatabaseHelper"**
4. **Look for:**
   - ✅ "✅ Firestore connection successful"
   - ✅ "User profile created successfully"
   - ❌ Any error messages

## 🚨 **Common Issues & Solutions**

### **Issue 1: "Permission denied" errors**

**Causes:**
- Firestore not enabled in Firebase Console
- Security rules too restrictive
- User not authenticated

**Solutions:**
1. **Enable Firestore:**
   - Go to Firebase Console → Firestore Database
   - Click "Create Database"
   - Choose "Start in test mode"

2. **Check Security Rules:**
   - Go to Firestore Database → Rules
   - Use the rules from `firestore_security_rules.txt`

3. **Verify Authentication:**
   - Make sure you're logged in to the app
   - Check if Firebase Auth is working

### **Issue 2: "Network error" messages**

**Causes:**
- No internet connection
- Firebase project not configured
- Wrong `google-services.json`

**Solutions:**
1. **Check internet connection**
2. **Verify `google-services.json`:**
   - Should be in `app/` folder
   - Should match your Firebase project
3. **Check Firebase project setup**

### **Issue 3: "Document not found" errors**

**Causes:**
- User profile doesn't exist yet
- Wrong document ID
- Security rules blocking access

**Solutions:**
1. **Create user profile first** using the test activities
2. **Check document IDs** in Firebase Console
3. **Verify security rules**

## 📊 **What to Look For in Firebase Console**

### **Collections You Should See:**

```
/users/{userId}
├── userId: "your-user-id"
├── email: "your-email@example.com"
├── displayName: "Your Name"
├── createdAt: timestamp
├── lastSeen: timestamp
├── isOnline: true
├── profilePicture: ""
└── status: "Hey there! I'm using StegoChat!"

/conversations/{conversationId}
├── participants: ["user1", "user2"]
├── createdAt: timestamp
├── lastMessage: "Test message"
└── lastMessageTime: timestamp

/messages/{messageId}
├── conversationId: "conversation-id"
├── senderId: "your-user-id"
├── message: "Test message"
├── messageType: "text"
├── timestamp: timestamp
└── isRead: false
```

## 🧪 **Step-by-Step Testing Process**

### **Step 1: Basic Connectivity**
1. **Run the app**
2. **Login with Firebase account**
3. **Tap "⚡ Quick Firestore Test"**
4. **Verify it shows "✅ FIRESTORE WORKING!"**

### **Step 2: Profile Operations**
1. **Tap "💾 Firestore Example"**
2. **Tap "👤 Create User Profile"**
3. **Tap "📋 Get User Profile"**
4. **Verify profile data is displayed**

### **Step 3: Message Operations**
1. **Enter a test message**
2. **Tap "💬 Send Test Message"**
3. **Verify success message**

### **Step 4: Check Firebase Console**
1. **Go to Firebase Console**
2. **Navigate to Firestore Database**
3. **Verify collections and documents exist**

## 🔧 **Debugging Tips**

### **Enable Debug Logging:**
```kotlin
// Add this to your activity
firebaseHelper.testFirebaseConnection()
```

### **Check Authentication Status:**
```kotlin
val isAuthenticated = firebaseHelper.isUserAuthenticated()
val currentUser = firebaseHelper.getCurrentUser()
Log.d("TEST", "Authenticated: $isAuthenticated, User: ${currentUser?.email}")
```

### **Test Individual Operations:**
```kotlin
// Test profile creation
val success = firebaseHelper.createUserProfile(userId, email, displayName)
Log.d("TEST", "Profile creation: $success")

// Test profile retrieval
val profile = firebaseHelper.getUserProfile(userId)
Log.d("TEST", "Profile: $profile")
```

## ✅ **Success Checklist**

Your Firestore is working correctly if:

- ✅ **Quick test shows "✅ FIRESTORE WORKING!"**
- ✅ **Can create user profiles**
- ✅ **Can retrieve user profiles**
- ✅ **Can send test messages**
- ✅ **See data in Firebase Console**
- ✅ **No "Permission denied" errors**
- ✅ **No "Network error" messages**

## 🆘 **Getting Help**

If you're still having issues:

1. **Check the error messages** in the test activities
2. **Look at Logcat** for detailed error information
3. **Verify Firebase Console** setup
4. **Check security rules** are properly configured
5. **Ensure you're authenticated** in the app

## 🎯 **Next Steps After Testing**

Once Firestore is working:

1. **Set up security rules** for production
2. **Test with multiple users**
3. **Implement real-time listeners**
4. **Add offline support**
5. **Monitor usage** in Firebase Console

Your Firestore integration is ready to use! 🚀


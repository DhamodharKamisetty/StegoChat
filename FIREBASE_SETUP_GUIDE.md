# Firebase Setup Guide for Stegochat

## Quick Fix for Storage Bucket Issues

If you're getting "upgrade project billing plan" when trying to enable Storage, it's likely because:

1. **Previous project deletion**: When you delete a Firebase project, the default Storage bucket name gets temporarily locked
2. **Bucket name conflicts**: Firebase reserves bucket names for a period after deletion
3. **Multiple project confusion**: Having multiple projects can cause configuration conflicts

### Solution: Fresh Project Approach

**Step 1: Delete Current Project**
- Go to Firebase Console → Project Settings → General → Delete project
- Confirm deletion

**Step 2: Create New Project**
1. Go to https://console.firebase.google.com/
2. Click "Create a project"
3. Name it something like "stegochat-new" or "stegochat-v2"
4. Follow the setup wizard

**Step 3: Enable Services (in this order)**
1. **Authentication**
   - Build → Authentication → Get started
   - Sign-in method → Email/Password → Enable → Save

2. **Firestore Database**
   - Build → Firestore Database → Create database
   - Start in test mode → Choose location → Done

3. **Storage**
   - Build → Storage → Get started
   - Start in test mode → Choose location → Done

**Step 4: Add Android App**
1. Project Overview → Add app → Android
2. Package name: `com.example.stegochat`
3. Download `google-services.json`
4. Place in `app/` folder

**Step 5: Update Security Rules**

Firestore Rules:
```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /{document=**} {
      allow read, write: if request.auth != null;
    }
  }
}
```

Storage Rules:
```
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    match /{allPaths=**} {
      allow read, write: if request.auth != null;
    }
  }
}
```

## Original Setup Instructions

## 🎯 **What You'll Show Your College**

### **Live Database Dashboard**
- **Real-time user registration** and authentication
- **Live friend requests** and connections
- **Real-time chat messages** storage
- **Stego image uploads** to cloud storage
- **User activity tracking** and analytics

### **Professional Backend Features**
- **Cloud-hosted database** (Firestore)
- **User authentication** system
- **File storage** (Firebase Storage)
- **Real-time updates** and notifications
- **Scalable architecture** for production

## 🚀 **Step-by-Step Firebase Setup**

### **Step 1: Create Firebase Project**

1. **Go to** [Firebase Console]()https://console.firebase.google.com/
2. **Click** "Create a project"
3. **Enter project name**: `StegoChat-Backend`
4. **Enable Google Analytics** (optional but recommended)
5. **Click** "Create project"

### **Step 2: Add Android App**

1. **Click** "Add app" → "Android"
2. **Enter package name**: `com.example.stegochat`
3. **Enter app nickname**: `StegoChat`
4. **Click** "Register app"
5. **Download** `google-services.json` file
6. **Place** `google-services.json` in `app/` folder

### **Step 3: Enable Firebase Services**

#### **Authentication**
1. **Go to** Authentication → Sign-in method
2. **Enable** Email/Password
3. **Enable** Google Sign-in (optional)

#### **Firestore Database**
1. **Go to** Firestore Database
2. **Click** "Create database"
3. **Choose** "Start in test mode"
4. **Select** location (closest to your region)

#### **Storage**
1. **Go to** Storage
2. **Click** "Get started"
3. **Choose** "Start in test mode"
4. **Select** location

### **Step 4: Set Security Rules**

#### **Firestore Rules**
```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Users can read/write their own data
    match /users/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
    
    // Friend requests
    match /friend_requests/{requestId} {
      allow read, write: if request.auth != null;
    }
    
    // Friends
    match /friends/{friendId} {
      allow read, write: if request.auth != null;
    }
    
    // Messages
    match /messages/{messageId} {
      allow read, write: if request.auth != null;
    }
    
    // Stego images
    match /stego_images/{imageId} {
      allow read, write: if request.auth != null;
    }
  }
}
```

#### **Storage Rules**
```javascript
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    match /stego_images/{userId}/{allPaths=**} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
  }
}
```

## 📱 **What Your App Now Does**

### **1. User Management**
- ✅ **User registration** with email/password
- ✅ **User profiles** stored in Firestore
- ✅ **Authentication** state management
- ✅ **Last seen** and online status

### **2. Friend System**
- ✅ **Send friend requests** to other users
- ✅ **Accept/decline** friend requests
- ✅ **Friends list** stored in database
- ✅ **Real-time updates** when friends come online

### **3. Chat System**
- ✅ **Send/receive messages** in real-time
- ✅ **Message history** stored in Firestore
- ✅ **Conversation management**
- ✅ **Read receipts** and timestamps

### **4. Stego Image Storage**
- ✅ **Upload stego images** to Firebase Storage
- ✅ **Image metadata** stored in Firestore
- ✅ **Secure access** to user's own images
- ✅ **Cloud backup** of all stego images

## 🎓 **College Presentation Points**

### **Technical Architecture**
- **Frontend**: Android Native (Kotlin)
- **Backend**: Firebase (Google Cloud)
- **Database**: Firestore (NoSQL)
- **Storage**: Firebase Storage
- **Authentication**: Firebase Auth
- **Real-time**: Firestore listeners

### **Database Collections**
```
📁 users/           ← User profiles and settings
📁 friends/         ← Friend relationships
📁 friend_requests/ ← Pending friend requests
📁 conversations/   ← Chat conversations
📁 messages/        ← Individual chat messages
📁 stego_images/    ← Steganography images
```

### **Live Demo Scenarios**
1. **User Registration**: Show new user appearing in Firebase Console
2. **Friend Request**: Send request, show it in database
3. **Real-time Chat**: Send message, show it appearing instantly
4. **Image Upload**: Upload stego image, show in storage
5. **Data Persistence**: Restart app, show data still there

## 🔧 **Testing Your Backend**

### **1. Build and Run**
```bash
# Sync Gradle
./gradlew clean build

# Run on device/emulator
./gradlew installDebug
```

### **2. Test Features**
- **Register** a new user
- **Login** with credentials
- **Send** a friend request
- **Upload** a stego image
- **Send** a chat message

### **3. Verify in Firebase Console**
- **Authentication** → Users tab
- **Firestore** → Data tab
- **Storage** → Files tab
- **Analytics** → Dashboard

## 📊 **What Professors Will See**

### **Professional Features**
- ✅ **Cloud-hosted backend** (not just local storage)
- ✅ **Real-time database** with live updates
- ✅ **User authentication** system
- ✅ **File storage** and management
- ✅ **Scalable architecture** design

### **Technical Skills Demonstrated**
- ✅ **Firebase integration** and configuration
- ✅ **Cloud database** design and implementation
- ✅ **Real-time data** synchronization
- ✅ **Security rules** and access control
- ✅ **File upload** and storage management

### **Production Ready**
- ✅ **User management** system
- ✅ **Data persistence** across devices
- ✅ **Scalable backend** architecture
- ✅ **Professional security** implementation
- ✅ **Real-world** application development

## 🚨 **Important Notes**

### **Free Tier Limits**
- **Firestore**: 1GB storage, 50K reads/day, 20K writes/day
- **Storage**: 5GB storage, 1GB downloads/day
- **Authentication**: 10K users/month
- **Perfect for college projects!**

### **Security**
- **Test mode** rules for development
- **Production rules** for deployment
- **User authentication** required for all operations
- **Data isolation** between users

## 🎉 **You're Ready!**

Your StegoChat app now has:
- 🔥 **Professional Firebase backend**
- 📱 **Real-time database operations**
- 🔐 **User authentication system**
- ☁️ **Cloud storage for images**
- 📊 **Live data dashboard**

**Perfect for impressing your college professors with a real, production-ready backend!**

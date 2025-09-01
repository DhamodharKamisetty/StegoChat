# Adding Your App to Firebase (Step by Step)

## Step 1: Create New Firebase Project

1. **Go to Firebase Console**: https://console.firebase.google.com/
2. **Click "Create a project"**
3. **Enter project name**: `stegochat-new` (or any name you want)
4. **Enable Google Analytics**: ✅ Yes (recommended)
5. **Click "Create project"**
6. **Wait for project to be created**

## Step 2: Add Android App

1. **On project overview page, click "Add app"**
2. **Select Android icon** (📱)
3. **Enter package name**: `com.example.stegochat`
4. **Enter app nickname**: `StegoChat`
5. **Click "Register app"**

## Step 3: Download google-services.json

1. **Download the `google-services.json` file**
2. **Place it in your project**: `app/google-services.json`
3. **Click "Next"**
4. **Skip the SDK setup** (we already have it)
5. **Click "Continue to console"**

## Step 4: Enable Authentication

1. **In left sidebar, click "Authentication"**
2. **Click "Get started"**
3. **Click "Sign-in method" tab**
4. **Click "Email/Password"**
5. **Toggle "Enable" to ON**
6. **Click "Save"**

## Step 5: Enable Firestore Database

1. **In left sidebar, click "Firestore Database"**
2. **Click "Create database"**
3. **Choose "Start in test mode"**
4. **Select location** (choose closest to you)
5. **Click "Done"**

## Step 6: Set Firestore Security Rules

1. **In Firestore Database, click "Rules" tab**
2. **Replace the rules with**:

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

3. **Click "Publish"**

## Step 7: Test Your App

1. **Build your app**: `./gradlew assembleDebug`
2. **Run on your phone**
3. **Try to create an account**
4. **Check Firebase Console → Authentication → Users**

## What You'll Have:

✅ **User Authentication** - Users can register/login  
✅ **User Database** - User profiles stored in Firestore  
✅ **Real-time Data** - Live updates in Firebase Console  
✅ **Professional Backend** - Perfect for college presentation  

## For Storage (Alternative):

Since Firebase Storage has issues, we'll use **Local Storage** for images:

- Images saved to phone storage
- Works offline
- No internet needed
- Perfect for demo

## Next Steps:

1. **Test the app** - Create an account and login
2. **Check Firebase Console** - See users appearing
3. **Show your professor** - Live database with real users!

Your app will work perfectly with just Auth + Firestore. The stego images will be saved locally on the phone, which is actually great for a demo!



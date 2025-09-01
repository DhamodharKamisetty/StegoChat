# Firestore Security Rules Testing Guide

## 🧪 Testing Your Security Rules

After setting up your security rules, it's important to test them to ensure they work correctly.

## 📋 What These Rules Do

### ✅ **User Management**
- Users can only read/write their own profile
- Users can read other users' basic info (for friend suggestions)

### ✅ **Friend System**
- Users can only access friend requests they sent or received
- Users can only access friendships they're part of

### ✅ **Chat System**
- Users can only access conversations they're participants in
- Users can only send messages as themselves

### ✅ **Stego Images**
- Users can only access their own stego images

## 🧪 How to Test the Rules

### Step 1: Test with Your App

1. **Run your StegoChat app**
2. **Login with a test account**
3. **Navigate to "💾 Firestore Example"**
4. **Test each operation:**

#### Test User Profile Operations:
- ✅ **Create Profile** - Should work for authenticated user
- ✅ **Get Profile** - Should work for own profile
- ✅ **Update Status** - Should work for own profile

#### Test Message Operations:
- ✅ **Send Message** - Should work for authenticated user
- ✅ **Create Conversation** - Should work for authenticated user

### Step 2: Test with Firebase Console

1. **Go to Firebase Console → Firestore Database**
2. **Click "Start collection"**
3. **Try to create documents manually:**

#### Test User Document:
```
Collection: users
Document ID: test_user_123
Fields:
- userId: "test_user_123"
- email: "test@example.com"
- displayName: "Test User"
```

**Expected Result:** ❌ Should fail (no authentication in console)

### Step 3: Test with Firebase Emulator (Advanced)

If you want to test more thoroughly:

```bash
# Install Firebase CLI if you haven't
npm install -g firebase-tools

# Login to Firebase
firebase login

# Initialize Firebase in your project
firebase init firestore

# Start the emulator
firebase emulators:start --only firestore
```

## 🔍 Common Testing Scenarios

### Scenario 1: User Authentication
```javascript
// Test: Unauthenticated user trying to read user profile
// Expected: ❌ DENIED
db.collection('users').doc('user123').get()
```

### Scenario 2: User Accessing Own Profile
```javascript
// Test: Authenticated user reading own profile
// Expected: ✅ ALLOWED
db.collection('users').doc(auth.currentUser.uid).get()
```

### Scenario 3: User Accessing Other User's Profile
```javascript
// Test: Authenticated user reading other user's profile
// Expected: ✅ ALLOWED (read only)
db.collection('users').doc('other_user_123').get()
```

### Scenario 4: User Modifying Other User's Profile
```javascript
// Test: Authenticated user trying to modify other user's profile
// Expected: ❌ DENIED
db.collection('users').doc('other_user_123').update({status: "Hacked!"})
```

## 🚨 Troubleshooting Common Issues

### Issue 1: "Permission denied" errors in your app

**Possible Causes:**
- User not authenticated
- Trying to access data they don't own
- Rules not published yet

**Solutions:**
1. Check if user is logged in: `firebaseHelper.isUserAuthenticated()`
2. Verify the user ID matches the document owner
3. Wait a few minutes after publishing rules

### Issue 2: Rules not taking effect

**Solutions:**
1. Make sure you clicked "Publish" in Firebase Console
2. Wait 1-2 minutes for rules to propagate
3. Clear your app's cache and restart

### Issue 3: Can't read other users' profiles

**Expected Behavior:** Users should be able to read other users' basic info for friend suggestions.

**If it's not working:**
1. Check the rules for the `users` collection
2. Ensure the user is authenticated
3. Verify the document exists

## 📊 Monitoring Rules Usage

### View Rule Usage in Firebase Console:

1. **Go to Firebase Console → Firestore Database**
2. **Click "Usage" tab**
3. **Monitor:**
   - Read operations
   - Write operations
   - Denied requests

### Check Logs:

1. **Go to Firebase Console → Functions** (if you have any)
2. **View logs for denied requests**
3. **Look for security rule violations**

## 🔧 Advanced Rule Testing

### Test with Multiple Users:

1. **Create two test accounts**
2. **Login with User A**
3. **Try to access User B's data**
4. **Verify appropriate access/denial**

### Test Edge Cases:

1. **Malformed data**
2. **Missing required fields**
3. **Invalid user IDs**
4. **Large data payloads**

## ✅ Success Indicators

Your security rules are working correctly if:

1. ✅ **Authenticated users can access their own data**
2. ✅ **Users cannot access other users' private data**
3. ✅ **Unauthenticated users are denied access**
4. ✅ **Friend system works correctly**
5. ✅ **Chat system works correctly**
6. ✅ **No "Permission denied" errors for legitimate operations**

## 🆘 Getting Help

If you encounter issues:

1. **Check Firebase Console logs**
2. **Test with the provided example activities**
3. **Verify your rules syntax**
4. **Ensure you're authenticated in your app**

## 🎯 Next Steps

After testing your rules:

1. **Monitor usage in Firebase Console**
2. **Set up alerts for security violations**
3. **Regularly review and update rules**
4. **Consider implementing additional security measures**

Your Firestore security is now properly configured! 🔒


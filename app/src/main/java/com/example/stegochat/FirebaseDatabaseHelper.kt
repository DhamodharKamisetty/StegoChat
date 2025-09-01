package com.example.stegochat

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirebaseDatabaseHelper {
    companion object {
        private const val TAG = "FirebaseDatabaseHelper"
        
        // Collections
        private const val USERS_COLLECTION = "users"
        private const val FRIENDS_COLLECTION = "friends"
        private const val FRIEND_REQUESTS_COLLECTION = "friend_requests"
        private const val CONVERSATIONS_COLLECTION = "conversations"
        private const val MESSAGES_COLLECTION = "messages"
        private const val STEGO_IMAGES_COLLECTION = "stego_images"
        
        // Get Firebase instances
        private val auth: FirebaseAuth = FirebaseAuth.getInstance()
        private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    }
    
    // ==================== AUTHENTICATION ====================
    
    /**
     * Get current authenticated user
     */
    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }
    
    /**
     * Get current user ID
     */
    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }
    
    /**
     * Check if user is authenticated
     */
    fun isUserAuthenticated(): Boolean {
        return auth.currentUser != null
    }
    
    /**
     * Sign out user
     */
    fun signOutUser() {
        auth.signOut()
    }
    
    // ==================== USER MANAGEMENT ====================
    
    /**
     * Create user profile in Firestore
     */
    suspend fun createUserProfile(userId: String, email: String, displayName: String): Boolean {
        return try {
            val userData = hashMapOf(
                "userId" to userId,
                "email" to email,
                "displayName" to displayName,
                "createdAt" to com.google.firebase.Timestamp.now(),
                "lastSeen" to com.google.firebase.Timestamp.now(),
                "isOnline" to true,
                "profilePicture" to "",
                "status" to "Hey there! I'm using StegoChat!"
            )
            
            firestore.collection(USERS_COLLECTION)
                .document(userId)
                .set(userData)
                .await()
            
            Log.d(TAG, "User profile created successfully")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error creating user profile: ${e.message}")
            false
        }
    }
    
    /**
     * Get user profile by ID
     */
    suspend fun getUserProfile(userId: String): Map<String, Any>? {
        return try {
            val document = firestore.collection(USERS_COLLECTION)
                .document(userId)
                .get()
                .await()
            
            document.data
        } catch (e: Exception) {
            Log.e(TAG, "Error getting user profile: ${e.message}")
            null
        }
    }
    
    /**
     * Update user profile
     */
    suspend fun updateUserProfile(userId: String, updates: Map<String, Any>): Boolean {
        return try {
            firestore.collection(USERS_COLLECTION)
                .document(userId)
                .update(updates)
                .await()
            
            Log.d(TAG, "User profile updated successfully")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error updating user profile: ${e.message}")
            false
        }
    }
    
    /**
     * Update user's last seen and online status
     */
    suspend fun updateUserStatus(userId: String, isOnline: Boolean) {
        try {
            val updates = hashMapOf<String, Any>(
                "lastSeen" to com.google.firebase.Timestamp.now(),
                "isOnline" to isOnline
            )
            
            firestore.collection(USERS_COLLECTION)
                .document(userId)
                .update(updates)
                .await()
        } catch (e: Exception) {
            Log.e(TAG, "Error updating user status: ${e.message}")
        }
    }
    
    /**
     * Get all users (for friend suggestions)
     */
    suspend fun getAllUsers(): List<Map<String, Any?>> {
        return try {
            val currentUserId = getCurrentUserId()
            val snapshot = firestore.collection(USERS_COLLECTION)
                .get()
                .await()
            
            snapshot.documents.mapNotNull { doc ->
                val data = doc.data
                if (data != null && doc.id != currentUserId) {
                    data + ("userId" to doc.id)
                } else null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting all users: ${e.message}")
            emptyList()
        }
    }
    
    // ==================== FRIEND SYSTEM ====================
    
    /**
     * Send friend request
     */
    suspend fun sendFriendRequest(fromUserId: String, toUserId: String): Boolean {
        return try {
            val requestId = "${fromUserId}_${toUserId}"
            val requestData = hashMapOf(
                "requestId" to requestId,
                "fromUserId" to fromUserId,
                "toUserId" to toUserId,
                "status" to "pending",
                "createdAt" to com.google.firebase.Timestamp.now()
            )
            
            firestore.collection(FRIEND_REQUESTS_COLLECTION)
                .document(requestId)
                .set(requestData)
                .await()
            
            Log.d(TAG, "Friend request sent successfully")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error sending friend request: ${e.message}")
            false
        }
    }
    
    /**
     * Accept friend request
     */
    suspend fun acceptFriendRequest(requestId: String): Boolean {
        return try {
            val requestDoc = firestore.collection(FRIEND_REQUESTS_COLLECTION)
                .document(requestId)
                .get()
                .await()
            
            val requestData = requestDoc.data
            if (requestData != null) {
                val fromUserId = requestData["fromUserId"] as String
                val toUserId = requestData["toUserId"] as String
                
                // Update request status
                firestore.collection(FRIEND_REQUESTS_COLLECTION)
                    .document(requestId)
                    .update("status", "accepted")
                    .await()
                
                // Add to friends collection
                val friendData = hashMapOf(
                    "userId1" to fromUserId,
                    "userId2" to toUserId,
                    "createdAt" to com.google.firebase.Timestamp.now()
                )
                
                firestore.collection(FRIENDS_COLLECTION)
                    .add(friendData)
                    .await()
                
                Log.d(TAG, "Friend request accepted successfully")
                true
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error accepting friend request: ${e.message}")
            false
        }
    }
    
    /**
     * Get pending friend requests for a user
     */
    suspend fun getPendingFriendRequests(userId: String): List<Map<String, Any?>> {
        return try {
            val snapshot = firestore.collection(FRIEND_REQUESTS_COLLECTION)
                .whereEqualTo("toUserId", userId)
                .whereEqualTo("status", "pending")
                .get()
                .await()
            
            snapshot.documents.mapNotNull { doc ->
                doc.data?.let { data ->
                    data + ("requestId" to doc.id)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting friend requests: ${e.message}")
            emptyList()
        }
    }
    
    /**
     * Get user's friends list
     */
    suspend fun getUserFriends(userId: String): List<Map<String, Any?>> {
        return try {
            val snapshot = firestore.collection(FRIENDS_COLLECTION)
                .whereEqualTo("userId1", userId)
                .get()
                .await()
            
            val friends1 = snapshot.documents.mapNotNull { doc ->
                doc.data?.let { data ->
                    data + ("friendId" to data["userId2"])
                }
            }
            
            val snapshot2 = firestore.collection(FRIENDS_COLLECTION)
                .whereEqualTo("userId2", userId)
                .get()
                .await()
            
            val friends2 = snapshot2.documents.mapNotNull { doc ->
                doc.data?.let { data ->
                    data + ("friendId" to data["userId1"])
                }
            }
            
            friends1 + friends2
        } catch (e: Exception) {
            Log.e(TAG, "Error getting user friends: ${e.message}")
            emptyList()
        }
    }
    
    // ==================== CHAT SYSTEM ====================
    
    /**
     * Create or get conversation between two users
     */
    suspend fun getOrCreateConversation(userId1: String, userId2: String): String {
        return try {
            // Check if conversation already exists (check both possible orders)
            val participants1 = listOf(userId1, userId2)
            val participants2 = listOf(userId2, userId1)
            
            // Try first order
            var snapshot = firestore.collection(CONVERSATIONS_COLLECTION)
                .whereEqualTo("participants", participants1)
                .get()
                .await()
            
            // If not found, try second order
            if (snapshot.isEmpty) {
                snapshot = firestore.collection(CONVERSATIONS_COLLECTION)
                    .whereEqualTo("participants", participants2)
                    .get()
                    .await()
            }
            
            if (!snapshot.isEmpty) {
                snapshot.documents[0].id
            } else {
                // Create new conversation
                val conversationData = hashMapOf(
                    "participants" to participants1, // Use consistent order
                    "createdAt" to com.google.firebase.Timestamp.now(),
                    "lastMessage" to "",
                    "lastMessageTime" to com.google.firebase.Timestamp.now()
                )
                
                val docRef = firestore.collection(CONVERSATIONS_COLLECTION)
                    .add(conversationData)
                    .await()
                
                Log.d(TAG, "New conversation created with ID: ${docRef.id}")
                docRef.id
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting/creating conversation: ${e.message}", e)
            ""
        }
    }
    
    /**
     * Send message in conversation
     */
    suspend fun sendMessage(conversationId: String, senderId: String, message: String, messageType: String = "text"): Boolean {
        return try {
            val messageData = hashMapOf(
                "conversationId" to conversationId,
                "senderId" to senderId,
                "message" to message,
                "messageType" to messageType,
                "timestamp" to com.google.firebase.Timestamp.now(),
                "isRead" to false
            )
            
            firestore.collection(MESSAGES_COLLECTION)
                .add(messageData)
                .await()
            
            // Update conversation's last message
            firestore.collection(CONVERSATIONS_COLLECTION)
                .document(conversationId)
                .update(
                    mapOf(
                        "lastMessage" to message,
                        "lastMessageTime" to com.google.firebase.Timestamp.now()
                    )
                )
                .await()
            
            Log.d(TAG, "Message sent successfully")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error sending message: ${e.message}")
            false
        }
    }
    
    /**
     * Get conversation messages
     */
    suspend fun getConversationMessages(conversationId: String): List<Map<String, Any?>> {
        return try {
            val messagesSnapshot = firestore.collection(MESSAGES_COLLECTION)
                .whereEqualTo("conversationId", conversationId)
                .orderBy("timestamp")
                .get()
                .await()
            
            messagesSnapshot.documents.map { doc ->
                doc.data ?: emptyMap()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting messages: ${e.message}")
            emptyList()
        }
    }
    
    /**
     * Get user's conversations
     */
    suspend fun getUserConversations(userId: String): List<Map<String, Any?>> {
        return try {
            val snapshot = firestore.collection(CONVERSATIONS_COLLECTION)
                .whereArrayContains("participants", userId)
                .orderBy("lastMessageTime", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .await()
            
            snapshot.documents.mapNotNull { doc ->
                doc.data?.let { data ->
                    data + ("conversationId" to doc.id)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting user conversations: ${e.message}")
            emptyList()
        }
    }
    
    // ==================== STEGO IMAGE METADATA ====================
    
    /**
     * Save stego image metadata (without actual file upload)
     */
    suspend fun saveStegoImageMetadata(
        userId: String,
        imagePath: String,
        originalMessage: String,
        recipientId: String?
    ): Boolean {
        return try {
            val imageData = hashMapOf(
                "userId" to userId,
                "localImagePath" to imagePath,
                "originalMessage" to originalMessage,
                "recipientId" to recipientId,
                "createdAt" to com.google.firebase.Timestamp.now(),
                "imageType" to "stego"
            )
            
            firestore.collection(STEGO_IMAGES_COLLECTION)
                .add(imageData)
                .await()
            
            Log.d(TAG, "Stego image metadata saved successfully")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error saving stego image metadata: ${e.message}")
            false
        }
    }
    
    /**
     * Get user's stego images
     */
    suspend fun getUserStegoImages(userId: String): List<Map<String, Any?>> {
        return try {
            val snapshot = firestore.collection(STEGO_IMAGES_COLLECTION)
                .whereEqualTo("userId", userId)
                .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .await()
            
            snapshot.documents.map { doc ->
                doc.data ?: emptyMap()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting user stego images: ${e.message}")
            emptyList()
        }
    }
    
    // ==================== UTILITY FUNCTIONS ====================
    
    /**
     * Test Firebase connection
     */
    fun testFirebaseConnection() {
        try {
            // Test Firestore connection
            firestore.collection("test").document("connection_test")
                .set(mapOf("timestamp" to com.google.firebase.Timestamp.now()))
                .addOnSuccessListener {
                    Log.d(TAG, "✅ Firestore connection successful")
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "❌ Firestore connection failed: ${e.message}")
                }
            
            // Test Auth connection
            Log.d(TAG, "✅ Firebase Auth initialized: ${auth.app.name}")
            
        } catch (e: Exception) {
            Log.e(TAG, "❌ Firebase test failed: ${e.message}")
        }
    }
}

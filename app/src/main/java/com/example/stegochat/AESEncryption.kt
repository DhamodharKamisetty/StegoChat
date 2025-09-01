package com.example.stegochat

import android.util.Base64
import java.security.KeyFactory
import java.security.PublicKey
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec
import java.security.MessageDigest
import java.security.Signature
import java.util.*
import org.json.JSONObject

object AESEncryption {
    
    private const val GCM_IV_LENGTH = 12
    private const val GCM_TAG_LENGTH = 16
    
    /**
     * Encrypt a message using AES-GCM with a randomly generated key,
     * then encrypt the AES key with the recipient's public key
     */
    fun encryptMessage(message: String, recipientPublicKey: String, senderPrivateKey: java.security.PrivateKey? = null): String {
        try {
            // Generate a random AES key
            val keyGenerator = KeyGenerator.getInstance("AES")
            keyGenerator.init(256)
            val aesKey = keyGenerator.generateKey()
            
            // Generate random IV for GCM
            val iv = ByteArray(GCM_IV_LENGTH)
            Random().nextBytes(iv)
            
            // Encrypt the message with AES-GCM
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            val gcmSpec = GCMParameterSpec(GCM_TAG_LENGTH * 8, iv)
            cipher.init(Cipher.ENCRYPT_MODE, aesKey, gcmSpec)
            val encryptedMessage = cipher.doFinal(message.toByteArray())
            
            // Encrypt the AES key with the recipient's public key
            val publicKey = getPublicKeyFromString(recipientPublicKey)
            val rsaCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
            rsaCipher.init(Cipher.ENCRYPT_MODE, publicKey)
            val encryptedAESKey = rsaCipher.doFinal(aesKey.encoded)
            
            // Create JSON payload
            val payload = JSONObject().apply {
                put("c", Base64.encodeToString(encryptedMessage, Base64.DEFAULT)) // ciphertext
                put("k", Base64.encodeToString(encryptedAESKey, Base64.DEFAULT)) // encrypted key
                put("i", Base64.encodeToString(iv, Base64.DEFAULT)) // IV
                put("t", System.currentTimeMillis()) // timestamp
            }
            
            // Add signature if sender private key is provided
            senderPrivateKey?.let { privateKey ->
                val signature = Signature.getInstance("SHA256withRSA")
                signature.initSign(privateKey)
                val dataToSign = "${payload.getString("c")}${payload.getString("k")}${payload.getString("i")}"
                signature.update(dataToSign.toByteArray())
                val signatureBytes = signature.sign()
                payload.put("s", Base64.encodeToString(signatureBytes, Base64.DEFAULT)) // signature
            }
            
            return payload.toString()
        } catch (e: Exception) {
            throw RuntimeException("Encryption failed", e)
        }
    }
    
    /**
     * Decrypt a message using the recipient's private key and the encrypted AES key
     */
    fun decryptMessage(encryptedPayload: String, privateKey: String): String {
        try {
            val payload = JSONObject(encryptedPayload)
            
            // Extract components
            val encryptedMessage = Base64.decode(payload.getString("c"), Base64.DEFAULT)
            val encryptedAESKey = Base64.decode(payload.getString("k"), Base64.DEFAULT)
            val iv = Base64.decode(payload.getString("i"), Base64.DEFAULT)
            
            // Verify signature if present
            if (payload.has("s")) {
                // Note: In a real app, you'd verify the signature using the sender's public key
                // For now, we'll skip verification for simplicity
                // val signature = payload.getString("s") // Unused for now
            }
            
            // Decrypt the AES key using private key
            val privateKeyObj = getPrivateKeyFromString(privateKey)
            val rsaCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
            rsaCipher.init(Cipher.DECRYPT_MODE, privateKeyObj)
            val decryptedAESKeyBytes = rsaCipher.doFinal(encryptedAESKey)
            
            // Reconstruct the AES key
            val aesKey = SecretKeySpec(decryptedAESKeyBytes, "AES")
            
            // Decrypt the message using AES-GCM
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            val gcmSpec = GCMParameterSpec(GCM_TAG_LENGTH * 8, iv)
            cipher.init(Cipher.DECRYPT_MODE, aesKey, gcmSpec)
            val decryptedMessageBytes = cipher.doFinal(encryptedMessage)
            
            return String(decryptedMessageBytes)
        } catch (e: Exception) {
            throw RuntimeException("Decryption failed", e)
        }
    }
    
    /**
     * Decrypt a message using a PrivateKey object (e.g., from Android Keystore)
     */
    fun decryptMessage(encryptedPayload: String, privateKey: java.security.PrivateKey): String {
        try {
            val payload = JSONObject(encryptedPayload)
            val encryptedMessage = Base64.decode(payload.getString("c"), Base64.DEFAULT)
            val encryptedAESKey = Base64.decode(payload.getString("k"), Base64.DEFAULT)
            val iv = Base64.decode(payload.getString("i"), Base64.DEFAULT)

            val rsaCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
            rsaCipher.init(Cipher.DECRYPT_MODE, privateKey)
            val decryptedAESKeyBytes = rsaCipher.doFinal(encryptedAESKey)
            val aesKey = SecretKeySpec(decryptedAESKeyBytes, "AES")

            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            val gcmSpec = GCMParameterSpec(GCM_TAG_LENGTH * 8, iv)
            cipher.init(Cipher.DECRYPT_MODE, aesKey, gcmSpec)
            val decryptedMessageBytes = cipher.doFinal(encryptedMessage)

            return String(decryptedMessageBytes)
        } catch (e: Exception) {
            throw RuntimeException("Decryption failed", e)
        }
    }
    
    /**
     * Verify the signature of a message
     */
    fun verifySignature(encryptedPayload: String, senderPublicKey: String): Boolean {
        return try {
            val payload = JSONObject(encryptedPayload)
            if (!payload.has("s")) return false
            
            val signature = Base64.decode(payload.getString("s"), Base64.DEFAULT)
            val dataToVerify = "${payload.getString("c")}${payload.getString("k")}${payload.getString("i")}"
            
            val publicKey = getPublicKeyFromString(senderPublicKey)
            val sig = Signature.getInstance("SHA256withRSA")
            sig.initVerify(publicKey)
            sig.update(dataToVerify.toByteArray())
            
            sig.verify(signature)
        } catch (e: Exception) {
            false
        }
    }
    
    private fun getPublicKeyFromString(publicKeyString: String): PublicKey {
        val keyBytes = Base64.decode(publicKeyString, Base64.DEFAULT)
        val keySpec = X509EncodedKeySpec(keyBytes)
        val keyFactory = KeyFactory.getInstance("RSA")
        return keyFactory.generatePublic(keySpec)
    }
    
    private fun getPrivateKeyFromString(privateKeyString: String): java.security.PrivateKey {
        val keyBytes = Base64.decode(privateKeyString, Base64.DEFAULT)
        val keySpec = java.security.spec.PKCS8EncodedKeySpec(keyBytes)
        val keyFactory = KeyFactory.getInstance("RSA")
        return keyFactory.generatePrivate(keySpec)
    }
    
    /**
     * Generate a key pair for testing purposes
     */
    fun generateKeyPair(): KeyPair {
        val keyPairGenerator = java.security.KeyPairGenerator.getInstance("RSA")
        keyPairGenerator.initialize(2048)
        val keyPair = keyPairGenerator.generateKeyPair()
        
        return KeyPair(
            publicKey = Base64.encodeToString(keyPair.public.encoded, Base64.DEFAULT),
            privateKey = Base64.encodeToString(keyPair.private.encoded, Base64.DEFAULT)
        )
    }
}

data class KeyPair(
    val publicKey: String,
    val privateKey: String
) 
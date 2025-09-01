package com.example.stegochat

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.PrivateKey
import java.security.PublicKey
import javax.crypto.Cipher

class KeyManager(private val context: Context) {
    
    companion object {
        private const val KEYSTORE_PROVIDER = "AndroidKeyStore"
        private const val KEY_ALIAS = "StegoChatRSAKey"
        private const val KEY_SIZE = 2048
    }
    
    /**
     * Generate and store RSA key pair in Android Keystore
     */
    fun generateKeyPair(): Boolean {
        return try {
            val keyPairGenerator = KeyPairGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_RSA, 
                KEYSTORE_PROVIDER
            )
            
            val spec = KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
                .setKeySize(KEY_SIZE)
                .build()
            
            keyPairGenerator.initialize(spec)
            keyPairGenerator.generateKeyPair()
            
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    /**
     * Get the stored public key as Base64 string
     */
    fun getPublicKeyString(): String? {
        return try {
            val keyStore = KeyStore.getInstance(KEYSTORE_PROVIDER)
            keyStore.load(null)
            
            val publicKey = keyStore.getCertificate(KEY_ALIAS)?.publicKey
            publicKey?.let {
                Base64.encodeToString(it.encoded, Base64.DEFAULT)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    /**
     * Get the stored private key
     */
    fun getPrivateKey(): PrivateKey? {
        return try {
            val keyStore = KeyStore.getInstance(KEYSTORE_PROVIDER)
            keyStore.load(null)
            
            keyStore.getKey(KEY_ALIAS, null) as? PrivateKey
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    /**
     * Check if key pair exists
     */
    fun hasKeyPair(): Boolean {
        return try {
            val keyStore = KeyStore.getInstance(KEYSTORE_PROVIDER)
            keyStore.load(null)
            keyStore.containsAlias(KEY_ALIAS)
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Delete the stored key pair
     */
    fun deleteKeyPair(): Boolean {
        return try {
            val keyStore = KeyStore.getInstance(KEYSTORE_PROVIDER)
            keyStore.load(null)
            keyStore.deleteEntry(KEY_ALIAS)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    /**
     * Encrypt data with public key
     */
    fun encryptWithPublicKey(data: ByteArray, publicKeyString: String): ByteArray? {
        return try {
            val publicKey = getPublicKeyFromString(publicKeyString)
            val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
            cipher.init(Cipher.ENCRYPT_MODE, publicKey)
            cipher.doFinal(data)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    /**
     * Decrypt data with private key
     */
    fun decryptWithPrivateKey(encryptedData: ByteArray): ByteArray? {
        return try {
            val privateKey = getPrivateKey()
            if (privateKey == null) return null
            
            val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
            cipher.init(Cipher.DECRYPT_MODE, privateKey)
            cipher.doFinal(encryptedData)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    private fun getPublicKeyFromString(publicKeyString: String): java.security.PublicKey {
        val keyBytes = Base64.decode(publicKeyString, Base64.DEFAULT)
        val keySpec = java.security.spec.X509EncodedKeySpec(keyBytes)
        val keyFactory = java.security.KeyFactory.getInstance("RSA")
        return keyFactory.generatePublic(keySpec)
    }
} 
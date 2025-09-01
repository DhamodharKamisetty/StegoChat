package com.example.stegochat

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.nio.charset.StandardCharsets
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import java.security.MessageDigest
import java.util.*

class LSBSteganography {
    companion object {
        private const val TAG = "LSBSteganography"
        private const val ENCRYPTION_KEY = "StegoChat2024!" // In production, use secure key management
        
        /**
         * Hide a message in an image using LSB steganography
         * @param context Android context for content URI handling
         * @param originalImagePath Path to the original image (can be file path or content URI)
         * @param message Message to hide
         * @param outputPath Path to save the stego image
         * @return true if successful, false otherwise
         */
        fun hideMessage(context: android.content.Context, originalImagePath: String, message: String, outputPath: String): Boolean {
            return try {
                Log.d(TAG, "Starting LSB encoding process")
                
                // Load the original image - handle both file paths and content URIs, with robust options
                val originalBitmap = loadBitmap(context, originalImagePath)
                
                if (originalBitmap == null) {
                    Log.e(TAG, "Failed to load original image from: $originalImagePath")
                    return false
                }
                
                // Create a mutable copy of the bitmap
                val mutableBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true)
                
                // Convert message to binary
                val messageBinary = stringToBinary(message)
                val messageLength = messageBinary.length
                
                Log.d(TAG, "Message length: $messageLength bits")
                
                // Check if message fits in image
                val maxCapacity = calculateMaxCapacity(context, originalImagePath)
                // Include 32-bit header for length
                if (messageLength + 32 > maxCapacity * 8) {
                    Log.e(TAG, "Message too large for image")
                    return false
                }
                
                // Convert message length to 32-bit binary (use unsigned approach)
                val lengthBinary = String.format("%32s", Integer.toBinaryString(messageLength)).replace(' ', '0')
                
                // Combine length and message
                val combinedBinary = lengthBinary + messageBinary
                
                Log.d(TAG, "Combined binary length: ${combinedBinary.length}")
                
                // Embed the binary data into the image using a more robust approach
                var bitIndex = 0
                for (i in combinedBinary.indices) {
                    val pixelIndex = bitIndex / 3
                    val x = pixelIndex % mutableBitmap.width
                    val y = pixelIndex / mutableBitmap.width
                    val channel = bitIndex % 3
                    
                    if (x < mutableBitmap.width && y < mutableBitmap.height) {
                        // Read from the mutable bitmap so prior channel edits on the same pixel are preserved
                        val pixel = mutableBitmap.getPixel(x, y)
                        val bit = combinedBinary[i]
                        val newPixel = modifyLSB(pixel, bit, when (channel) {
                            0 -> 'R'
                            1 -> 'G'
                            2 -> 'B'
                            else -> 'R'
                        })
                        mutableBitmap.setPixel(x, y, newPixel)
                    }
                    bitIndex++
                }
                
                // Save the stego image
                val success = saveBitmap(context, mutableBitmap, outputPath)
                if (success) {
                    Log.d(TAG, "Stego image saved successfully at: $outputPath")
                }
                
                // Clean up
                originalBitmap.recycle()
                mutableBitmap.recycle()
                
                success
            } catch (e: Exception) {
                Log.e(TAG, "Error in hideMessage: ${e.message}", e)
                false
            }
        }
        
        /**
         * Extract a hidden message from a stego image
         * @param context Android context for content URI handling
         * @param stegoImagePath Path to the stego image (can be file path or content URI)
         * @return The extracted message, or null if failed
         */
        fun extractMessage(context: android.content.Context, stegoImagePath: String): String? {
            return try {
                Log.d(TAG, "Starting LSB decoding process")
                
                // Load the stego image - handle both file paths and content URIs, with robust options and fallbacks
                var stegoBitmap = loadBitmap(context, stegoImagePath)
                if (stegoBitmap == null) {
                    Log.e(TAG, "Failed to load stego image from: $stegoImagePath")
                    return null
                }
                
                Log.d(TAG, "Stego bitmap size: ${stegoBitmap.width}x${stegoBitmap.height}")
                
                // Try extraction with primary channel order and a fallback order
                val channelOrders = listOf(
                    charArrayOf('R','G','B'),
                    charArrayOf('B','G','R'),
                    charArrayOf('G','R','B')
                )
                for (order in channelOrders) {
                    val result = tryExtractWithOrder(stegoBitmap, order)
                    if (result != null) {
                        Log.d(TAG, "Message extracted successfully with order ${order.joinToString("")}")
                        stegoBitmap.recycle()
                        return result
                    }
                }
                
                // Clean up
                stegoBitmap.recycle()
                null
            } catch (e: Exception) {
                Log.e(TAG, "Error in extractMessage: ${e.message}", e)
                null
            }
        }
        
        /**
         * Convert string to binary representation
         */
        private fun stringToBinary(input: String): String {
            val bytes = input.toByteArray(StandardCharsets.UTF_8)
            val binary = StringBuilder()
            for (byte in bytes) {
                binary.append(String.format("%8s", Integer.toBinaryString(byte.toInt() and 0xFF)).replace(' ', '0'))
            }
            return binary.toString()
        }
        
        /**
         * Convert binary representation back to string
         */
        private fun binaryToString(binary: String): String {
            val bytes = ByteArray(binary.length / 8)
            for (i in bytes.indices) {
                val start = i * 8
                val end = start + 8
                if (end <= binary.length) {
                    bytes[i] = Integer.parseInt(binary.substring(start, end), 2).toByte()
                }
            }
            return String(bytes, StandardCharsets.UTF_8)
        }
        
        /**
         * Modify the least significant bit of a specific color channel
         */
        private fun modifyLSB(pixel: Int, bit: Char, channel: Char): Int {
            val alpha = android.graphics.Color.alpha(pixel)
            val red = android.graphics.Color.red(pixel)
            val green = android.graphics.Color.green(pixel)
            val blue = android.graphics.Color.blue(pixel)
            
            return when (channel) {
                'R' -> android.graphics.Color.argb(alpha, 
                    if (bit == '1') red or 1 else red and 0xFE, green, blue)
                'G' -> android.graphics.Color.argb(alpha, red, 
                    if (bit == '1') green or 1 else green and 0xFE, blue)
                'B' -> android.graphics.Color.argb(alpha, red, green, 
                    if (bit == '1') blue or 1 else blue and 0xFE)
                else -> pixel
            }
        }

        /**
         * Load a bitmap from a file path or content URI with robust decode options and fallbacks
         */
        private fun loadBitmap(context: android.content.Context, pathOrUri: String): Bitmap? {
            return try {
                val options = BitmapFactory.Options().apply {
                    inPreferredConfig = Bitmap.Config.ARGB_8888
                    inDither = false
                    inMutable = false
                }
                if (pathOrUri.startsWith("content://")) {
                    val uri = android.net.Uri.parse(pathOrUri)
                    context.contentResolver.openInputStream(uri)?.use { input ->
                        BitmapFactory.decodeStream(input, null, options)
                    } ?: run {
                        // Fallback: use file descriptor
                        context.contentResolver.openFileDescriptor(uri, "r")?.use { pfd ->
                            BitmapFactory.decodeFileDescriptor(pfd.fileDescriptor, null, options)
                        }
                    }
                } else {
                    BitmapFactory.decodeFile(pathOrUri, options)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to load bitmap: ${e.message}")
                null
            }
        }

        /**
         * Try to extract message using a specific channel order
         */
        private fun tryExtractWithOrder(stegoBitmap: Bitmap, order: CharArray): String? {
            return try {
                // Extract message length first (32 bits)
                var bitIndex = 0
                val lengthBinary = StringBuilder()
                for (i in 0 until 32) {
                    val pixelIndex = bitIndex / order.size
                    val x = pixelIndex % stegoBitmap.width
                    val y = pixelIndex / stegoBitmap.width
                    val channelIdx = bitIndex % order.size
                    if (x < stegoBitmap.width && y < stegoBitmap.height) {
                        val pixel = stegoBitmap.getPixel(x, y)
                        val bit = extractLSB(pixel, order[channelIdx])
                        lengthBinary.append(bit)
                    } else {
                        lengthBinary.append('0')
                    }
                    bitIndex++
                }

                val binaryString = lengthBinary.toString()
                val lengthLong = try {
                    binaryString.toLong(2)
                } catch (e: NumberFormatException) {
                    return null
                }
                if (lengthLong <= 0 || lengthLong > (stegoBitmap.width * stegoBitmap.height * order.size)) {
                    return null
                }
                val messageLength = lengthLong.toInt()

                // Extract message bits
                val messageBinary = StringBuilder()
                for (i in 0 until messageLength) {
                    val pixelIndex = bitIndex / order.size
                    val x = pixelIndex % stegoBitmap.width
                    val y = pixelIndex / stegoBitmap.width
                    val channelIdx = bitIndex % order.size
                    if (x < stegoBitmap.width && y < stegoBitmap.height) {
                        val pixel = stegoBitmap.getPixel(x, y)
                        val bit = extractLSB(pixel, order[channelIdx])
                        messageBinary.append(bit)
                    } else {
                        return null
                    }
                    bitIndex++
                }

                binaryToString(messageBinary.toString()).trim { it <= ' ' }
            } catch (e: Exception) {
                null
            }
        }
        
        /**
         * Extract the least significant bit from a specific color channel
         */
        private fun extractLSB(pixel: Int, channel: Char): Char {
            return when (channel) {
                'R' -> if (android.graphics.Color.red(pixel) and 1 == 1) '1' else '0'
                'G' -> if (android.graphics.Color.green(pixel) and 1 == 1) '1' else '0'
                'B' -> if (android.graphics.Color.blue(pixel) and 1 == 1) '1' else '0'
                else -> '0'
            }
        }
        
        /**
         * Save bitmap to file or content URI
         */
        private fun saveBitmap(context: android.content.Context, bitmap: Bitmap, filePath: String): Boolean {
            return try {
                if (filePath.startsWith("content://")) {
                    // Handle content URI
                    val uri = android.net.Uri.parse(filePath)
                    val outputStream = context.contentResolver.openOutputStream(uri)
                    if (outputStream != null) {
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                        outputStream.flush()
                        outputStream.close()
                        true
                    } else {
                        Log.e(TAG, "Failed to open output stream for content URI: $filePath")
                        false
                    }
                } else {
                    // Handle file path
                    val file = File(filePath)
                    val outputStream = FileOutputStream(file)
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                    outputStream.flush()
                    outputStream.close()
                    true
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to save bitmap: ${e.message}", e)
                false
            }
        }
        
        /**
         * Calculate maximum message capacity for an image
         */
        fun calculateMaxCapacity(context: android.content.Context, imagePath: String): Int {
            return try {
                val bitmap = if (imagePath.startsWith("content://")) {
                    val uri = android.net.Uri.parse(imagePath)
                    val inputStream = context.contentResolver.openInputStream(uri)
                    if (inputStream != null) {
                        BitmapFactory.decodeStream(inputStream)
                    } else {
                        Log.e(TAG, "Failed to open input stream for content URI: $imagePath")
                        null
                    }
                } else {
                    BitmapFactory.decodeFile(imagePath)
                }
                if (bitmap != null) {
                    val capacity = bitmap.width * bitmap.height * 3 / 8 // 3 channels, 8 bits per byte
                    bitmap.recycle()
                    capacity
                } else {
                    0
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error calculating capacity: ${e.message}", e)
                0
            }
        }
    }
} 
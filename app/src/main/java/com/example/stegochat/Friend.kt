package com.example.stegochat

data class Friend(
    val id: String,
    val name: String,
    val publicKey: String,
    val profileImage: Int? = null,
    val isOnline: Boolean = false,
    val lastSeen: String? = null
) 
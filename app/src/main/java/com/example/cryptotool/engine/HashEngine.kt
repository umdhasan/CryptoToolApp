package com.example.cryptotool.engine

import java.security.MessageDigest

object HashEngine {
    fun generateSha256(input: String): String {
        return try {
            val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
            bytes.joinToString("") { "%02x".format(it) }
        } catch (e: Exception) {
            "Error: ${e.localizedMessage}"
        }
    }
}

package com.example.cryptotool.engine

import java.security.MessageDigest

object HashEngine {

    // ৪টি সিড একত্র করে SHA-512 হ্যাশ তৈরি করার ফাংশন
    fun generateSha512(serverSeed: String, c1: String, c2: String, c3: String): String {
        return try {
            // সার্ভার সিড ও ৩ জন খেলোয়াড়ের সিড সরাসরি যুক্ত করা হয়
            val combinedInput = serverSeed.trim() + c1.trim() + c2.trim() + c3.trim()
            val md = MessageDigest.getInstance("SHA-512")
            val bytes = md.digest(combinedInput.toByteArray(Charsets.UTF_8))
            bytes.joinToString("") { "%02x".format(it) }
        } catch (e: Exception) {
            "Error: ${e.localizedMessage}"
        }
    }

    // Aviator / Spribe স্ট্যান্ডার্ড ক্র্যাশ পয়েন্ট ফর্মুলা
    fun calculateCrashPoint(hash: String): Double {
        return try {
            // হ্যাশের প্রথম ১৩টি হেক্স ক্যারেক্টার (৫২ বিট)
            val subHash = hash.substring(0, 13)
            val decimalValue = subHash.toLong(16).toDouble()
            val twoPower52 = Math.pow(2.0, 52.0)

            // যদি মানটি ৩৩ দ্বারা নিঃশেষে বিভাজ্য হয় তবে ক্র্যাশ পয়েন্ট ১.০০x
            if (decimalValue.toLong() % 33L == 0L) {
                return 1.00
            }

            // ৩% হাউস এজ সমীকরণ: (100 - 3) * 2^52 / (2^52 - decimalValue)
            val result = (0.97 * twoPower52) / (twoPower52 - decimalValue)
            Math.floor(result * 100.0) / 100.0
        } catch (e: Exception) {
            1.00
        }
    }
}

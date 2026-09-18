package com.example.cryptotool.engine

import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object HashEngine {

    // HMAC-SHA256 হ্যাশ তৈরি করার ফাংশন
    fun generateHmacSha256(key: String, message: String): String {
        return try {
            val sha256Hmac = Mac.getInstance("HmacSHA256")
            val secretKey = SecretKeySpec(key.toByteArray(Charsets.UTF_8), "HmacSHA256")
            sha256Hmac.init(secretKey)
            val hashBytes = sha256Hmac.doFinal(message.toByteArray(Charsets.UTF_8))
            hashBytes.joinToString("") { "%02x".format(it) }
        } catch (e: Exception) {
            "Error: ${e.localizedMessage}"
        }
    }

    // হ্যাশ থেকে ক্র্যাশ গুণক (Multiplier) বের করার স্ট্যান্ডার্ড ফর্মুলা
    fun calculateCrashPoint(hash: String): Double {
        return try {
            // হ্যাশের প্রথম ১৩টি হেক্স ক্যারেক্টার (৫২ বিট) নেওয়া হয়
            val subHash = hash.substring(0, 13)
            val decimalValue = subHash.toLong(16).toDouble()
            val twoPower52 = Math.pow(2.0, 52.0)

            // ১% হাউস এজের প্রোভ্যাব্লি ফেয়ার সমীকরণ
            val result = (100.0 * twoPower52 - decimalValue) / (twoPower52 - decimalValue) / 100.0
            Math.floor(result * 100.0) / 100.0
        } catch (e: Exception) {
            1.00
        }
    }
}

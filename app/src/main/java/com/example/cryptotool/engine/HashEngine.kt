package com.example.cryptotool.engine

import java.security.MessageDigest

data class MathAnalysis(
    val decimalValue: Long,
    val isInstantCrash: Boolean,
    val remainder33: Long,
    val rawMultiplier: Double,
    val finalMultiplier: Double,
    val winProbability: Double
)

object HashEngine {

    // ১. সার্ভার সিড প্রি-ভেরিফিকেশন (SHA-256)
    fun verifyServerSeedHash(serverSeed: String, publishedHash: String): Boolean {
        if (serverSeed.isBlank() || publishedHash.isBlank()) return false
        val computedHash = sha256(serverSeed.trim())
        return computedHash.equals(publishedHash.trim(), ignoreCase = true)
    }

    private fun sha256(input: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        val bytes = md.digest(input.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }

    // ২. সার্ভার সিড ও ৩ জন খেলোয়াড়ের সিড মিলিয়ে SHA-512 হ্যাশ
    fun generateSha512(serverSeed: String, c1: String, c2: String, c3: String): String {
        return try {
            val combinedInput = serverSeed.trim() + c1.trim() + c2.trim() + c3.trim()
            val md = MessageDigest.getInstance("SHA-512")
            val bytes = md.digest(combinedInput.toByteArray(Charsets.UTF_8))
            bytes.joinToString("") { "%02x".format(it) }
        } catch (e: Exception) {
            "Error: ${e.localizedMessage}"
        }
    }

    // ৩. ৩% হাউস এজ ও ১.০০x ক্র্যাশ গাণিতিক বিশ্লেষণ
    fun analyzeCrashPoint(hash: String): MathAnalysis {
        return try {
            val subHash = hash.substring(0, 13)
            val decimalValue = subHash.toLong(16)
            val twoPower52 = Math.pow(2.0, 52.0)

            val remainder = decimalValue % 33L
            val isInstantCrash = (remainder == 0L)

            if (isInstantCrash) {
                return MathAnalysis(
                    decimalValue = decimalValue,
                    isInstantCrash = true,
                    remainder33 = remainder,
                    rawMultiplier = 1.00,
                    finalMultiplier = 1.00,
                    winProbability = 3.03
                )
            }

            // ৩% হাউস এজ সূত্র: (0.97 * 2^52) / (2^52 - X)
            val raw = (0.97 * twoPower52) / (twoPower52 - decimalValue.toDouble())
            val finalVal = Math.floor(raw * 100.0) / 100.0
            val probability = if (finalVal > 0) (0.97 / finalVal) * 100.0 else 0.0

            MathAnalysis(
                decimalValue = decimalValue,
                isInstantCrash = false,
                remainder33 = remainder,
                rawMultiplier = Math.floor(raw * 1000.0) / 1000.0,
                finalMultiplier = finalVal,
                winProbability = Math.floor(probability * 100.0) / 100.0
            )
        } catch (e: Exception) {
            MathAnalysis(0L, false, -1L, 1.00, 1.00, 0.0)
        }
    }

    // ৪. টার্গেট গুণকের বাস্তব সম্ভাবনা (%) হিসাব
    fun estimateTargetProbability(targetMultiplier: Double): Double {
        if (targetMultiplier <= 1.00) return 100.0
        val prob = (0.97 / targetMultiplier) * 100.0
        return Math.floor(prob * 100.0) / 100.0
    }
}

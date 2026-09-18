package com.example.cryptotool.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cryptotool.engine.HashEngine

// পূর্ববর্তী রাউন্ডের হিস্ট্রি সংরক্ষণের ডেটা ক্লাস
data class RoundHistoryItem(
    val roundNumber: Int,
    val multiplier: String,
    val hexValue: String
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProvablyFairScreen()
        }
    }
}

@Composable
fun ProvablyFairScreen() {
    val clipboardManager = LocalClipboardManager.current

    var serverSeed by remember { mutableStateOf("") }
    var client1 by remember { mutableStateOf("") }
    var client2 by remember { mutableStateOf("") }
    var client3 by remember { mutableStateOf("") }

    var resultMultiplier by remember { mutableStateOf("") }
    var sha512Hash by remember { mutableStateOf("") }
    var hexVal by remember { mutableStateOf("") }
    var decVal by remember { mutableStateOf("") }

    // হিস্ট্রি তালিকা
    val historyList = remember { mutableStateListOf<RoundHistoryItem>() }

    val darkBg = Color(0xFF121212)
    val cardBg = Color(0xFF1E1E1E)
    val neonGreen = Color(0xFF00E676)
    val lightRed = Color(0xFFFF5252)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(darkBg)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Aviator Fair Verifier",
            style = MaterialTheme.typography.headlineMedium,
            color = neonGreen
        )

        Spacer(modifier = Modifier.height(8.dp))

        // উপরে আলাদা Clear বাটন
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            OutlinedButton(
                onClick = {
                    serverSeed = ""
                    client1 = ""
                    client2 = ""
                    client3 = ""
                    resultMultiplier = ""
                    sha512Hash = ""
                    hexVal = ""
                    decVal = ""
                },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = lightRed),
                border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(lightRed))
            ) {
                Text("🧹 সব মুছুন (Clear)")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Server Seed Field
        OutlinedTextField(
            value = serverSeed,
            onValueChange = { serverSeed = it },
            label = { Text("Server Seed", color = Color.Gray) },
            trailingIcon = {
                TextButton(onClick = {
                    clipboardManager.getText()?.text?.let { serverSeed = it.trim() }
                }) {
                    Text("Paste", color = neonGreen, fontSize = 12.sp)
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = neonGreen,
                unfocusedBorderColor = Color.Gray
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Player 1 Seed Field
        OutlinedTextField(
            value = client1,
            onValueChange = { client1 = it },
            label = { Text("Player 1 Seed", color = Color.Gray) },
            trailingIcon = {
                TextButton(onClick = {
                    clipboardManager.getText()?.text?.let { client1 = it.trim() }
                }) {
                    Text("Paste", color = neonGreen, fontSize = 12.sp)
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = neonGreen,
                unfocusedBorderColor = Color.Gray
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Player 2 Seed Field
        OutlinedTextField(
            value = client2,
            onValueChange = { client2 = it },
            label = { Text("Player 2 Seed", color = Color.Gray) },
            trailingIcon = {
                TextButton(onClick = {
                    clipboardManager.getText()?.text?.let { client2 = it.trim() }
                }) {
                    Text("Paste", color = neonGreen, fontSize = 12.sp)
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = neonGreen,
                unfocusedBorderColor = Color.Gray
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Player 3 Seed Field
        OutlinedTextField(
            value = client3,
            onValueChange = { client3 = it },
            label = { Text("Player 3 Seed", color = Color.Gray) },
            trailingIcon = {
                TextButton(onClick = {
                    clipboardManager.getText()?.text?.let { client3 = it.trim() }
                }) {
                    Text("Paste", color = neonGreen, fontSize = 12.sp)
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = neonGreen,
                unfocusedBorderColor = Color.Gray
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (serverSeed.isNotBlank()) {
                    val hash = HashEngine.generateSha512(serverSeed, client1, client2, client3)
                    sha512Hash = hash
                    if (hash.length >= 13) {
                        val sub = hash.substring(0, 13)
                        hexVal = sub
                        decVal = sub.toLong(16).toString()
                        val crashPoint = HashEngine.calculateCrashPoint(hash)
                        val multiplierText = "${crashPoint}x"
                        resultMultiplier = multiplierText

                        // নতুন রাউন্ড হিস্ট্রি তালিকায় সবার উপরে যোগ করা
                        historyList.add(
                            0,
                            RoundHistoryItem(
                                roundNumber = historyList.size + 1,
                                multiplier = multiplierText,
                                hexValue = sub
                            )
                        )
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = neonGreen),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("ফলাফল যাচাই করুন", color = Color.Black)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // বর্তমান রাউন্ডের ফলাফল কার্ড
        Card(
            colors = CardDefaults.cardColors(containerColor = cardBg),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "ক্র্যাশ গুণক (Result):", color = Color.LightGray)
                Text(
                    text = resultMultiplier.ifEmpty { "---" },
                    color = neonGreen,
                    style = MaterialTheme.typography.headlineLarge
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(text = "Hex (প্রথম ১৩ ক্যারেক্টার):", color = Color.LightGray)
                Text(text = hexVal.ifEmpty { "---" }, color = Color.White)

                Spacer(modifier = Modifier.height(8.dp))

                Text(text = "Decimal মান:", color = Color.LightGray)
                Text(text = decVal.ifEmpty { "---" }, color = Color.White)

                Spacer(modifier = Modifier.height(8.dp))

                Text(text = "Combined SHA-512 Hash:", color = Color.LightGray)
                Text(
                    text = sha512Hash.ifEmpty { "---" },
                    color = Color.White,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        // হিস্ট্রি সেকশন
        if (historyList.isNotEmpty()) {
            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "যাচাইকৃত রাউন্ডের ইতিহাস (History)",
                style = MaterialTheme.typography.titleMedium,
                color = neonGreen,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(8.dp))

            historyList.take(10).forEach { item ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = cardBg),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "রাউন্ড #${item.roundNumber}", color = Color.Gray, fontSize = 12.sp)
                            Text(text = "Hex: ${item.hexValue}", color = Color.LightGray, fontSize = 12.sp)
                        }
                        Text(
                            text = item.multiplier,
                            color = neonGreen,
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                }
            }
        }
    }
}

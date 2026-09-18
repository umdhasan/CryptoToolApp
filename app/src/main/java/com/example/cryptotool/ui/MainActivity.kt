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
import androidx.compose.ui.unit.dp
import com.example.cryptotool.engine.HashEngine

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
    var serverSeed by remember { mutableStateOf("") }
    var clientSeed by remember { mutableStateOf("") }
    var nonce by remember { mutableStateOf("") }

    var resultMultiplier by remember { mutableStateOf("") }
    var hmacHash by remember { mutableStateOf("") }

    val darkBg = Color(0xFF121212)
    val cardBg = Color(0xFF1E1E1E)
    val neonGreen = Color(0xFF00E676)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(darkBg)
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Provably Fair Verifier",
            style = MaterialTheme.typography.headlineMedium,
            color = neonGreen
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = serverSeed,
            onValueChange = { serverSeed = it },
            label = { Text("Server Seed (গোপন কি)", color = Color.Gray) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = neonGreen,
                unfocusedBorderColor = Color.Gray
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = clientSeed,
            onValueChange = { clientSeed = it },
            label = { Text("Client Seed", color = Color.Gray) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = neonGreen,
                unfocusedBorderColor = Color.Gray
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = nonce,
            onValueChange = { nonce = it },
            label = { Text("Nonce (রাউন্ড নম্বর)", color = Color.Gray) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = neonGreen,
                unfocusedBorderColor = Color.Gray
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                if (serverSeed.isNotBlank()) {
                    val message = if (nonce.isNotBlank()) "$clientSeed:$nonce" else clientSeed
                    val hash = HashEngine.generateHmacSha256(serverSeed.trim(), message.trim())
                    hmacHash = hash
                    val crashPoint = HashEngine.calculateCrashPoint(hash)
                    resultMultiplier = "${crashPoint}x"
                } else {
                    resultMultiplier = "Server Seed দিন"
                    hmacHash = ""
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = neonGreen),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("ফলাফল ও হ্যাশ যাচাই করুন", color = Color.Black)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = cardBg),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "ক্র্যাশ গুণক (Multiplier):",
                    color = Color.LightGray,
                    style = MaterialTheme.typography.labelLarge
                )
                Text(
                    text = resultMultiplier.ifEmpty { "---" },
                    color = neonGreen,
                    style = MaterialTheme.typography.headlineLarge
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "প্রমাণিক HMAC-SHA256 হ্যাশ:",
                    color = Color.LightGray,
                    style = MaterialTheme.typography.labelSmall
                )
                Text(
                    text = hmacHash.ifEmpty { "এখানে সম্পূর্ণ হ্যাশ প্রদর্শিত হবে" },
                    color = Color.White,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

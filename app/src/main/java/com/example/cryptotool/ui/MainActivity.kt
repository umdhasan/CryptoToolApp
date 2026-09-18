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
    var client1 by remember { mutableStateOf("") }
    var client2 by remember { mutableStateOf("") }
    var client3 by remember { mutableStateOf("") }

    var resultMultiplier by remember { mutableStateOf("") }
    var sha512Hash by remember { mutableStateOf("") }
    var hexVal by remember { mutableStateOf("") }
    var decVal by remember { mutableStateOf("") }

    val darkBg = Color(0xFF121212)
    val cardBg = Color(0xFF1E1E1E)
    val neonGreen = Color(0xFF00E676)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(darkBg)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Aviator Fair Verifier",
            style = MaterialTheme.typography.headlineMedium,
            color = neonGreen
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = serverSeed,
            onValueChange = { serverSeed = it },
            label = { Text("Server Seed", color = Color.Gray) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = neonGreen,
                unfocusedBorderColor = Color.Gray
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = client1,
            onValueChange = { client1 = it },
            label = { Text("Player 1 Seed", color = Color.Gray) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = neonGreen,
                unfocusedBorderColor = Color.Gray
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = client2,
            onValueChange = { client2 = it },
            label = { Text("Player 2 Seed", color = Color.Gray) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = neonGreen,
                unfocusedBorderColor = Color.Gray
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = client3,
            onValueChange = { client3 = it },
            label = { Text("Player 3 Seed", color = Color.Gray) },
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
                        resultMultiplier = "${crashPoint}x"
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = neonGreen),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("ফলাফল যাচাই করুন", color = Color.Black)
        }

        Spacer(modifier = Modifier.height(16.dp))

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
    }
}

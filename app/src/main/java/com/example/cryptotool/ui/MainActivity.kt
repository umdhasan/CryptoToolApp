package com.example.cryptotool.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
            CryptoToolScreen()
        }
    }
}

@Composable
fun CryptoToolScreen() {
    var inputText by remember { mutableStateOf("") }
    var hashResult by remember { mutableStateOf("") }

    val darkBg = Color(0xFF121212)
    val neonGreen = Color(0xFF00E676)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(darkBg)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Crypto Hash Tool",
            style = MaterialTheme.typography.headlineMedium,
            color = neonGreen
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = inputText,
            onValueChange = { inputText = it },
            label = { Text("ইনপুট টেক্সট লিখুন", color = Color.Gray) },
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
                hashResult = HashEngine.generateSha256(inputText)
            },
            colors = ButtonDefaults.buttonColors(containerColor = neonGreen)
        ) {
            Text("হ্যাশ তৈরি করুন", color = Color.Black)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "ফলাফল (SHA-256):",
            color = Color.LightGray
        )
        Text(
            text = hashResult.ifEmpty { "এখানে হ্যাশ প্রদর্শিত হবে" },
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

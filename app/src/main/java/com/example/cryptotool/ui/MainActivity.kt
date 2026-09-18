package com.example.cryptotool.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
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
import androidx.compose.ui.viewinterop.AndroidView
import com.example.cryptotool.engine.HashEngine
import com.example.cryptotool.engine.MathAnalysis

data class RoundHistoryItem(
    val roundNumber: Int,
    val multiplier: String,
    val hexValue: String
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainTabApp()
        }
    }
}

@Composable
fun MainTabApp() {
    var selectedTab by remember { mutableStateOf(0) }
    val darkBg = Color(0xFF121212)
    val neonGreen = Color(0xFF00E676)

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = Color(0xFF1E1E1E)) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    label = { Text("🎮 লাইভ গেম") },
                    icon = { Text("🎮", fontSize = 18.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedTextColor = neonGreen,
                        indicatorColor = Color(0xFF2C2C2C)
                    )
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    label = { Text("🔍 সততা যাচাই") },
                    icon = { Text("🔍", fontSize = 18.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedTextColor = neonGreen,
                        indicatorColor = Color(0xFF2C2C2C)
                    )
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(darkBg)
                .padding(paddingValues)
        ) {
            when (selectedTab) {
                0 -> LiveGameScreen()
                1 -> VerifierScreen()
            }
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun LiveGameScreen() {
    val gameUrl = "https://www.ezcashbdv1.com/"

    // ওয়েবভিউ সম্পূর্ণ স্বাধীনভাবে লোড হবে, তাই কিবোর্ড উঠলেও কোনো ফোকাস সরবে না
    AndroidView(
        factory = { context ->
            WebView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                webChromeClient = WebChromeClient()
                webViewClient = WebViewClient()

                settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    databaseEnabled = true
                    useWideViewPort = true
                    loadWithOverviewMode = true
                    cacheMode = WebSettings.LOAD_DEFAULT
                    mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                    userAgentString = "Mozilla/5.0 (Linux; Android 13; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Mobile Safari/537.36"
                }
                loadUrl(gameUrl)
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
fun VerifierScreen() {
    val clipboardManager = LocalClipboardManager.current

    var preRoundHash by remember { mutableStateOf("") }
    var serverSeed by remember { mutableStateOf("") }
    var client1 by remember { mutableStateOf("") }
    var client2 by remember { mutableStateOf("") }
    var client3 by remember { mutableStateOf("") }

    var targetGuess by remember { mutableStateOf("2.00") }
    var guessProbability by remember { mutableStateOf("48.5%") }

    var isSeedVerified by remember { mutableStateOf<Boolean?>(null) }
    var resultMultiplier by remember { mutableStateOf("") }
    var sha512Hash by remember { mutableStateOf("") }
    var hexVal by remember { mutableStateOf("") }
    var mathDetails by remember { mutableStateOf<MathAnalysis?>(null) }

    val historyList = remember { mutableStateListOf<RoundHistoryItem>() }

    val cardBg = Color(0xFF1E1E1E)
    val neonGreen = Color(0xFF00E676)
    val lightRed = Color(0xFFFF5252)
    val cyanAccent = Color(0xFF00E5FF)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Aviator Verifier",
                style = MaterialTheme.typography.titleLarge,
                color = neonGreen
            )

            OutlinedButton(
                onClick = {
                    preRoundHash = ""
                    serverSeed = ""
                    client1 = ""
                    client2 = ""
                    client3 = ""
                    resultMultiplier = ""
                    sha512Hash = ""
                    hexVal = ""
                    isSeedVerified = null
                    mathDetails = null
                },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = lightRed)
            ) {
                Text("🧹 সব মুছুন", fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // টার্গেট প্রেডিকশন / সম্ভাব্যতা কার্ড
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF16231C)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "🎯 টার্গেট গুণক সম্ভাব্যতা ক্যালকুলেটর",
                    color = cyanAccent,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = targetGuess,
                        onValueChange = {
                            targetGuess = it
                            val target = it.toDoubleOrNull() ?: 0.0
                            if (target > 0) {
                                guessProbability = "${HashEngine.estimateTargetProbability(target)}%"
                            }
                        },
                        label = { Text("টার্গেট (যেমন: 2.0x)", color = Color.Gray, fontSize = 11.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = cyanAccent
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("বাস্তব সম্ভাবনা:", color = Color.Gray, fontSize = 11.sp)
                        Text(guessProbability, color = neonGreen, style = MaterialTheme.typography.titleLarge)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Pre-round Hash ফিল্ড
        OutlinedTextField(
            value = preRoundHash,
            onValueChange = { preRoundHash = it },
            label = { Text("Pre-round Server Seed Hash (SHA-256)", color = Color.Gray) },
            trailingIcon = {
                TextButton(onClick = {
                    clipboardManager.getText()?.text?.let { preRoundHash = it.trim() }
                }) {
                    Text("Paste", color = neonGreen, fontSize = 12.sp)
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = cyanAccent,
                unfocusedBorderColor = Color.DarkGray
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Server Seed ফিল্ড
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

        // ৩ জন খেলোয়াড়ের সিড
        listOf(
            Triple("Player 1 Seed", client1) { text: String -> client1 = text },
            Triple("Player 2 Seed", client2) { text: String -> client2 = text },
            Triple("Player 3 Seed", client3) { text: String -> client3 = text }
        ).forEach { (label, value, onUpdate) ->
            OutlinedTextField(
                value = value,
                onValueChange = onUpdate,
                label = { Text(label, color = Color.Gray) },
                trailingIcon = {
                    TextButton(onClick = {
                        clipboardManager.getText()?.text?.let { onUpdate(it.trim()) }
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
            Spacer(modifier = Modifier.height(6.dp))
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = {
                if (serverSeed.isNotBlank()) {
                    isSeedVerified = if (preRoundHash.isNotBlank()) {
                        HashEngine.verifyServerSeedHash(serverSeed, preRoundHash)
                    } else {
                        null
                    }

                    val hash = HashEngine.generateSha512(serverSeed, client1, client2, client3)
                    sha512Hash = hash
                    if (hash.length >= 13) {
                        hexVal = hash.substring(0, 13)
                        val analysis = HashEngine.analyzeCrashPoint(hash)
                        mathDetails = analysis
                        val multiplierText = "${analysis.finalMultiplier}x"
                        resultMultiplier = multiplierText

                        historyList.add(
                            0,
                            RoundHistoryItem(
                                roundNumber = historyList.size + 1,
                                multiplier = multiplierText,
                                hexValue = hexVal
                            )
                        )
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = neonGreen),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("ফলাফল ও সততা যাচাই করুন", color = Color.Black, style = MaterialTheme.typography.titleMedium)
        }

        Spacer(modifier = Modifier.height(14.dp))

        // রেজাল্ট কার্ড
        Card(
            colors = CardDefaults.cardColors(containerColor = cardBg),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                if (isSeedVerified != null) {
                    Text(
                        text = if (isSeedVerified == true) "✅ সার্ভার সিড ১০০% খাঁটি" else "❌ সার্ভার সিড মেলেনি",
                        color = if (isSeedVerified == true) neonGreen else lightRed,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Text(text = "ক্র্যাশ গুণক (Crash Multiplier):", color = Color.LightGray)
                Text(
                    text = resultMultiplier.ifEmpty { "---" },
                    color = neonGreen,
                    style = MaterialTheme.typography.headlineLarge
                )

                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Hex: $hexVal", color = Color.White, fontSize = 13.sp)
                Text(text = "SHA-512 Hash:\n${sha512Hash.take(32)}...", color = Color.Gray, fontSize = 11.sp)
            }
        }

        // হিস্ট্রি
        if (historyList.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text("যাচাইকৃত ইতিহাস (History)", color = neonGreen, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            historyList.take(5).forEach { item ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = cardBg),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("রাউন্ড #${item.roundNumber} (Hex: ${item.hexValue})", color = Color.Gray, fontSize = 12.sp)
                        Text(item.multiplier, color = neonGreen, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

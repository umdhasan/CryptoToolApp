package com.example.cryptotool.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
            ProvablyFairMasterScreen()
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun ProvablyFairMasterScreen() {
    val clipboardManager = LocalClipboardManager.current

    var preRoundHash by remember { mutableStateOf("") }
    var serverSeed by remember { mutableStateOf("") }
    var client1 by remember { mutableStateOf("") }
    var client2 by remember { mutableStateOf("") }
    var client3 by remember { mutableStateOf("") }

    var isSeedVerified by remember { mutableStateOf<Boolean?>(null) }
    var resultMultiplier by remember { mutableStateOf("") }
    var sha512Hash by remember { mutableStateOf("") }
    var hexVal by remember { mutableStateOf("") }
    var mathDetails by remember { mutableStateOf<MathAnalysis?>(null) }

    var showLiveScreen by remember { mutableStateOf(false) }
    val gameUrl = "https://ceobd9.com/m/hom"

    val historyList = remember { mutableStateListOf<RoundHistoryItem>() }

    val darkBg = Color(0xFF121212)
    val cardBg = Color(0xFF1E1E1E)
    val neonGreen = Color(0xFF00E676)
    val lightRed = Color(0xFFFF5252)
    val cyanAccent = Color(0xFF00E5FF)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(darkBg)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Aviator Fair Master",
            style = MaterialTheme.typography.headlineMedium,
            color = neonGreen
        )

        Spacer(modifier = Modifier.height(10.dp))

        // কন্ট্রোল বাটন
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { showLiveScreen = !showLiveScreen },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (showLiveScreen) lightRed else cyanAccent
                )
            ) {
                Text(
                    text = if (showLiveScreen) "🔴 গেম লুকান" else "📺 লাইভ স্ক্রিন",
                    color = Color.Black,
                    fontSize = 12.sp
                )
            }

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

        // অপ্টিমাইজড লাইভ স্ক্রিন
        if (showLiveScreen) {
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = cardBg),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text(
                        text = "লাইভ গেম ইন্টারফেস (Hardware Accelerated)",
                        color = cyanAccent,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(380.dp)
                            .clip(RoundedCornerShape(8.dp))
                    ) {
                        AndroidView(
                            factory = { context ->
                                WebView(context).apply {
                                    setLayerType(View.LAYER_TYPE_HARDWARE, null)

                                    settings.apply {
                                        javaScriptEnabled = true
                                        domStorageEnabled = true
                                        databaseEnabled = true
                                        useWideViewPort = true
                                        loadWithOverviewMode = true
                                        setSupportZoom(false)
                                        builtInZoomControls = false
                                        displayZoomControls = false
                                        cacheMode = WebSettings.LOAD_DEFAULT
                                        mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                                        userAgentString = "Mozilla/5.0 (Linux; Android 13; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36"
                                    }

                                    webViewClient = object : WebViewClient() {
                                        override fun onPageFinished(view: WebView?, url: String?) {
                                            super.onPageFinished(view, url)
                                            view?.loadUrl("javascript:(function() { document.body.style.margin='0'; document.body.style.padding='0'; })()")
                                        }
                                    }
                                    loadUrl(gameUrl)
                                }
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Pre-round Hash ইনপুট
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

        // Server Seed ইনপুট
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

        // ৩ জন প্লেয়ারের সিড ইনপুট
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

        // ভেরিফাই বাটন
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
            Text(
                "ফলাফল ও সততা যাচাই করুন",
                color = Color.Black,
                style = MaterialTheme.typography.titleMedium
            )
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
                        text = if (isSeedVerified == true) "✅ সার্ভার সিড খাঁটি (Pre-round Hash মিলেছে)" else "❌ সার্ভার সিড মেলেনি (Tampered)",
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

        // গাণিতিক বিশ্লেষণ কার্ড
        mathDetails?.let { math ->
            Spacer(modifier = Modifier.height(14.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF18221C)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "🧮 গাণিতিক ও সম্ভাব্যতা বিশ্লেষণ",
                        color = cyanAccent,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = "• ডেসিমাল মান: ${math.decimalValue}", color = Color.White, fontSize = 12.sp)
                    Text(
                        text = "• ৩৩ বিভাজ্যতা: ${math.decimalValue} % 33 = ভাগশেষ ${math.remainder33}",
                        color = if (math.isInstantCrash) lightRed else Color.LightGray,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "• ৩% হাউস এজ সমীকরণ মান: ${math.rawMultiplier}x",
                        color = Color.LightGray,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "• গুণকটি আসার তাত্ত্বিক সম্ভাবনা: ${math.winProbability}%",
                        color = neonGreen,
                        fontSize = 13.sp
                    )
                }
            }
        }

        // হিস্ট্রি তালিকা
        if (historyList.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "যাচাইকৃত ইতিহাস (History)",
                style = MaterialTheme.typography.titleMedium,
                color = neonGreen,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(8.dp))
            historyList.take(5).forEach { item ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = cardBg),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "রাউন্ড #${item.roundNumber} (Hex: ${item.hexValue})", color = Color.Gray, fontSize = 12.sp)
                        Text(text = item.multiplier, color = neonGreen, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

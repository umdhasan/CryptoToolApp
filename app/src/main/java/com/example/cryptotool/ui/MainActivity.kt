package com.example.cryptotool.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
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
            AviatorSplitConsoleScreen()
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun AviatorSplitConsoleScreen() {
    val clipboardManager = LocalClipboardManager.current

    // স্টেটসমূহ
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

    // থিম রঙ
    val darkBg = Color(0xFF121212)
    val cardBg = Color(0xFF1E1E1E)
    val neonGreen = Color(0xFF00E676)
    val lightRed = Color(0xFFFF5252)
    val cyanAccent = Color(0xFF00E5FF)

    val gameUrl = "https://www.ezcashbdv1.com/"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(darkBg)
    ) {
        // ১. স্ক্রিনের প্রায় ৩০% অংশ জুড়ে লাইভ গেম উইন্ডো (সার্বক্ষণিক সচল ও অপরিবর্তনীয়)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.32f)
                .background(Color.Black)
        ) {
            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        setLayerType(View.LAYER_TYPE_HARDWARE, null)
                        webChromeClient = WebChromeClient()

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

                        webViewClient = object : WebViewClient() {
                            override fun onPageFinished(view: WebView?, url: String?) {
                                super.onPageFinished(view, url)
                                // ৩০% স্ক্রিনে বিমান এবং বাজি বাটন দুটোই সুন্দরভাবে ফিট করার জন্য অটো-স্কেল
                                view?.evaluateJavascript(
                                    """
                                    (function() {
                                        document.body.style.zoom = '80%';
                                    })();
                                    """.trimIndent(), null
                                )
                            }
                        }
                        loadUrl(gameUrl)
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        // ডিভাইডার ও কন্ট্রোল বার
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1A1A1A))
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "⚡ Verifier & Probability Console",
                color = neonGreen,
                fontSize = 12.sp,
                style = MaterialTheme.typography.titleSmall
            )

            TextButton(
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
                }
            ) {
                Text("🧹 সব মুছুন", color = lightRed, fontSize = 11.sp)
            }
        }

        // ২. নিচের ৭০% অংশ: স্ক্রোলযোগ্য সততা যাচাই ও অ্যানালাইসিস
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.68f)
                .padding(horizontal = 12.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // প্রেডিকশন ও সম্ভাব্যতার ধারণা কার্ড
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF16231C)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("🎯 টার্গেট প্রেডিকশন ধারণা:", color = cyanAccent, fontSize = 11.sp)
                        OutlinedTextField(
                            value = targetGuess,
                            onValueChange = {
                                targetGuess = it
                                val target = it.toDoubleOrNull() ?: 0.0
                                if (target > 0) {
                                    guessProbability = "${HashEngine.estimateTargetProbability(target)}%"
                                }
                            },
                            label = { Text("টার্গেট (যেমন: 2.0x)", color = Color.Gray, fontSize = 10.sp) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = cyanAccent
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(horizontalAlignment = Alignment.End) {
                        Text("বাস্তব সম্ভাবনা:", color = Color.Gray, fontSize = 11.sp)
                        Text(guessProbability, color = neonGreen, style = MaterialTheme.typography.titleLarge)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ইনপুট ফিল্ডসমূহ (ইন-বক্স পেস্ট বাটনসহ)
            OutlinedTextField(
                value = preRoundHash,
                onValueChange = { preRoundHash = it },
                label = { Text("Pre-round Server Seed Hash (SHA-256)", color = Color.Gray, fontSize = 11.sp) },
                trailingIcon = {
                    TextButton(onClick = {
                        clipboardManager.getText()?.text?.let { preRoundHash = it.trim() }
                    }) {
                        Text("Paste", color = neonGreen, fontSize = 11.sp)
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

            Spacer(modifier = Modifier.height(4.dp))

            OutlinedTextField(
                value = serverSeed,
                onValueChange = { serverSeed = it },
                label = { Text("Server Seed", color = Color.Gray, fontSize = 11.sp) },
                trailingIcon = {
                    TextButton(onClick = {
                        clipboardManager.getText()?.text?.let { serverSeed = it.trim() }
                    }) {
                        Text("Paste", color = neonGreen, fontSize = 11.sp)
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

            Spacer(modifier = Modifier.height(4.dp))

            listOf(
                Triple("Player 1 Seed", client1) { text: String -> client1 = text },
                Triple("Player 2 Seed", client2) { text: String -> client2 = text },
                Triple("Player 3 Seed", client3) { text: String -> client3 = text }
            ).forEach { (label, value, onUpdate) ->
                OutlinedTextField(
                    value = value,
                    onValueChange = onUpdate,
                    label = { Text(label, color = Color.Gray, fontSize = 11.sp) },
                    trailingIcon = {
                        TextButton(onClick = {
                            clipboardManager.getText()?.text?.let { onUpdate(it.trim()) }
                        }) {
                            Text("Paste", color = neonGreen, fontSize = 11.sp)
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
                Spacer(modifier = Modifier.height(4.dp))
            }

            Spacer(modifier = Modifier.height(8.dp))

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
                Text("ফলাফল ও সততা যাচাই করুন", color = Color.Black, style = MaterialTheme.typography.titleSmall)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ফলাফল কার্ড
            Card(
                colors = CardDefaults.cardColors(containerColor = cardBg),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    if (isSeedVerified != null) {
                        Text(
                            text = if (isSeedVerified == true) "✅ সার্ভার সিড খাঁটি" else "❌ সার্ভার সিড মেলেনি",
                            color = if (isSeedVerified == true) neonGreen else lightRed,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("ক্র্যাশ গুণক:", color = Color.LightGray, fontSize = 11.sp)
                            Text(
                                text = resultMultiplier.ifEmpty { "---" },
                                color = neonGreen,
                                style = MaterialTheme.typography.headlineMedium
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Hex: $hexVal", color = Color.White, fontSize = 11.sp)
                            Text("Hash: ${sha512Hash.take(16)}...", color = Color.Gray, fontSize = 10.sp)
                        }
                    }
                }
            }

            // ৩% হাউস এজ সমীকরণ বিশ্লেষণ কার্ড
            mathDetails?.let { math ->
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF18221C)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("🧮 গাণিতিক বিশ্লেষণ:", color = cyanAccent, fontSize = 11.sp)
                        Text("• ডেসিমাল: ${math.decimalValue}", color = Color.White, fontSize = 11.sp)
                        Text(
                            text = "• ৩৩ বিভাজ্যতা: ${math.decimalValue} % 33 = ভাগশেষ ${math.remainder33}",
                            color = if (math.isInstantCrash) lightRed else Color.LightGray,
                            fontSize = 11.sp
                        )
                        Text("• ৩% হাউস এজ সমীকরণ মান: ${math.rawMultiplier}x", color = Color.LightGray, fontSize = 11.sp)
                        Text("• এই গুণকটির বাস্তব সম্ভাবনা: ${math.winProbability}%", color = neonGreen, fontSize = 11.sp)
                    }
                }
            }

            // হিস্ট্রি তালিকা
            if (historyList.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "ইতিহাস (History)",
                    color = neonGreen,
                    fontSize = 12.sp,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(4.dp))
                historyList.take(5).forEach { item ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = cardBg),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("রাউন্ড #${item.roundNumber} (${item.hexValue})", color = Color.Gray, fontSize = 11.sp)
                            Text(item.multiplier, color = neonGreen, fontSize = 13.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

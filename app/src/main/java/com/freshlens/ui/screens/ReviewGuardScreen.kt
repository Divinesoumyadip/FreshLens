package com.freshlens.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewGuardScreen(onBack: () -> Unit = {}) {
    var reviewText by remember { mutableStateOf("") }
    var isAnalyzing by remember { mutableStateOf(false) }
    var showResult by remember { mutableStateOf(false) }

    // Mock result
    val genuinePercent = 67
    val botPercent = 33
    val manipulationScore = 42

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ReviewGuard", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1A1A2E),
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0F0F1A))
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                "Paste restaurant reviews to detect fake/bot-written content",
                color = Color.Gray, fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = reviewText,
                onValueChange = { reviewText = it },
                placeholder = { Text("Paste reviews here...", color = Color.Gray) },
                modifier = Modifier.fillMaxWidth().height(180.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF6C63FF),
                    unfocusedBorderColor = Color(0xFF2A2A3E),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color(0xFF6C63FF)
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    isAnalyzing = true
                    // Simulate analysis delay
                    showResult = true
                    isAnalyzing = false
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6C63FF)),
                shape = RoundedCornerShape(12.dp),
                enabled = reviewText.isNotBlank() && !isAnalyzing
            ) {
                if (isAnalyzing) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Icon(Icons.Default.Shield, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Analyze Reviews")
                }
            }

            if (showResult) {
                Spacer(modifier = Modifier.height(20.dp))

                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E30)),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("Analysis Result", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(16.dp))

                        // Genuine vs Bot bar
                        Text("Review Authenticity", color = Color.Gray, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(modifier = Modifier.fillMaxWidth().height(28.dp).clip(RoundedCornerShape(14.dp))) {
                            Box(modifier = Modifier.weight(genuinePercent.toFloat()).fillMaxHeight().background(Color(0xFF4CAF50))) {
                                Text("${genuinePercent}% Genuine", color = Color.White, fontSize = 11.sp,
                                    modifier = Modifier.align(Alignment.Center))
                            }
                            Box(modifier = Modifier.weight(botPercent.toFloat()).fillMaxHeight().background(Color(0xFFF44336))) {
                                Text("${botPercent}% Bot", color = Color.White, fontSize = 11.sp,
                                    modifier = Modifier.align(Alignment.Center))
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Manipulation score
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Manipulation Score", color = Color.Gray, fontSize = 12.sp)
                                Text("$manipulationScore/100", color = Color(0xFFFFC107), fontSize = 24.sp, fontWeight = FontWeight.Bold)
                            }
                            Surface(
                                color = Color(0xFF3E2E00),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    "MODERATE RISK",
                                    color = Color(0xFFFFC107),
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    fontSize = 11.sp, fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Divider(color = Color(0xFF2A2A3E))
                        Spacer(modifier = Modifier.height(12.dp))

                        Text("Key Findings", color = Color.White, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(8.dp))
                        FindingItem("Repetitive phrasing patterns detected in 3 reviews")
                        FindingItem("Unusual review spike on single date")
                        FindingItem("Generic positive language without specifics")
                    }
                }
            }
        }
    }
}

@Composable
fun FindingItem(text: String) {
    Row(modifier = Modifier.padding(vertical = 4.dp), verticalAlignment = Alignment.Top) {
        Text("•", color = Color(0xFF6C63FF), fontWeight = FontWeight.Bold, modifier = Modifier.padding(end = 8.dp, top = 2.dp))
        Text(text, color = Color(0xFFBBBBCC), fontSize = 13.sp)
    }
}

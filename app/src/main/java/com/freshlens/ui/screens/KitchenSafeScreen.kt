package com.freshlens.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class HygieneReport(
    val id: String,
    val violationType: String,
    val description: String,
    val date: String,
    val imageUrl: String?,
    val votes: Int
)

data class KitchenSafeData(
    val restaurantId: String,
    val restaurantName: String,
    val kitchenSafeScore: Int,
    val badge: String,
    val totalHygieneReports: Int,
    val fssaiLicense: String?,
    val fssaiValid: Boolean?,
    val reports: List<HygieneReport>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KitchenSafeScreen(
    restaurantId: String,
    onBack: () -> Unit = {}
) {
    // Mock data for UI — replace with ViewModel in production
    val data = KitchenSafeData(
        restaurantId = restaurantId,
        restaurantName = "Sample Restaurant",
        kitchenSafeScore = 72,
        badge = "YELLOW",
        totalHygieneReports = 3,
        fssaiLicense = "11224999000123",
        fssaiValid = true,
        reports = listOf(
            HygieneReport("1", "dirty_packaging", "Packaging had stains and moisture damage", "Apr 14, 2026", null, 12),
            HygieneReport("2", "temperature_abuse", "Food arrived cold, clearly not maintained at proper temp", "Apr 10, 2026", null, 8)
        )
    )

    val badgeColor = when (data.badge) {
        "GREEN" -> Color(0xFF4CAF50)
        "YELLOW" -> Color(0xFFFFC107)
        "RED" -> Color(0xFFF44336)
        "BLACKLISTED" -> Color(0xFF212121)
        else -> Color(0xFF9E9E9E)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("KitchenSafe", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1A1A2E),
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0F0F1A))
                .padding(padding)
        ) {
            // Score card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E30)),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(data.restaurantName, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(20.dp))

                        // Big score circle
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(120.dp)
                                .clip(RoundedCornerShape(60.dp))
                                .background(badgeColor.copy(alpha = 0.15f))
                                .border(3.dp, badgeColor, RoundedCornerShape(60.dp))
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    "${data.kitchenSafeScore}",
                                    color = badgeColor,
                                    fontSize = 36.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text("/100", color = Color.Gray, fontSize = 12.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Badge
                        Surface(
                            color = badgeColor,
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text(
                                data.badge,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "${data.totalHygieneReports} community reports",
                            color = Color.Gray, fontSize = 13.sp
                        )
                    }
                }
            }

            // FSSAI License card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (data.fssaiValid == true) Color(0xFF1B3A1B) else Color(0xFF3A1B1B)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            if (data.fssaiValid == true) Icons.Default.Verified else Icons.Default.Cancel,
                            contentDescription = null,
                            tint = if (data.fssaiValid == true) Color(0xFF4CAF50) else Color(0xFFF44336),
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                "FSSAI License",
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                data.fssaiLicense ?: "Not provided",
                                color = Color.Gray, fontSize = 12.sp
                            )
                            Text(
                                if (data.fssaiValid == true) "✓ Valid & Active" else "✗ Expired or Invalid",
                                color = if (data.fssaiValid == true) Color(0xFF4CAF50) else Color(0xFFF44336),
                                fontSize = 12.sp, fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Reports header
            item {
                Text(
                    "Community Reports",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
                )
            }

            // Report items
            items(data.reports) { report ->
                HygieneReportCard(report = report)
            }

            // Submit report button
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { /* Open report submission */ },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6C63FF)),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Default.ReportProblem, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Report a Hygiene Violation")
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun HygieneReportCard(report: HygieneReport) {
    val violationLabel = report.violationType.replace("_", " ").replaceFirstChar { it.uppercase() }
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E30)),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    color = Color(0xFF3E1F1F),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        violationLabel,
                        color = Color(0xFFFF7043),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 11.sp, fontWeight = FontWeight.Medium
                    )
                }
                Text(report.date, color = Color.Gray, fontSize = 11.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(report.description, color = Color(0xFFBBBBCC), fontSize = 13.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.ThumbUp, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("${report.votes} found this helpful", color = Color.Gray, fontSize = 11.sp)
            }
        }
    }
}

package com.freshlens.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.google.maps.android.heatmaps.HeatmapTileProvider

data class FraudPoint(val lat: Double, val lng: Double, val intensity: Float, val restaurantName: String, val fraudType: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeatMapScreen(onBack: () -> Unit = {}) {
    // Kolkata coords as default
    val kolkata = LatLng(22.5726, 88.3639)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(kolkata, 12f)
    }

    var selectedFilter by remember { mutableStateOf("All") }
    val filters = listOf("All", "Fake Photo", "Portion Fraud", "Fake Review", "Hygiene")

    // Mock fraud points — replaced by API data
    val fraudPoints = remember {
        listOf(
            FraudPoint(22.5726, 88.3639, 0.9f, "Restaurant A", "fake_photo"),
            FraudPoint(22.5800, 88.3700, 0.7f, "Restaurant B", "portion_fraud"),
            FraudPoint(22.5650, 88.3580, 0.5f, "Restaurant C", "fake_review"),
            FraudPoint(22.5900, 88.3800, 0.8f, "Restaurant D", "hygiene_violation"),
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("FraudHeatMap", fontWeight = FontWeight.Bold) },
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
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {

            // Google Maps
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(mapType = MapType.NORMAL),
                uiSettings = MapUiSettings(zoomControlsEnabled = false)
            ) {
                fraudPoints.forEach { point ->
                    val markerColor = when (point.fraudType) {
                        "fake_photo" -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
                        "portion_fraud" -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)
                        "fake_review" -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)
                        else -> BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)
                    }
                    Marker(
                        state = MarkerState(position = LatLng(point.lat, point.lng)),
                        title = point.restaurantName,
                        snippet = point.fraudType.replace("_", " "),
                        icon = markerColor
                    )
                }
            }

            // Filter chips
            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 8.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(horizontal = 12.dp)
                ) {
                    filters.forEach { filter ->
                        FilterChip(
                            selected = selectedFilter == filter,
                            onClick = { selectedFilter = filter },
                            label = { Text(filter, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF6C63FF),
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            // Legend card
            Card(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xDD1E1E30)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Legend", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    LegendItem(Color.Red, "Fake Photo")
                    LegendItem(Color(0xFFFF9800), "Portion Fraud")
                    LegendItem(Color.Yellow, "Fake Review")
                    LegendItem(Color(0xFF9C27B0), "Hygiene")
                }
            }

            // Stats card
            Card(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xDD1E1E30)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("${fraudPoints.size}", color = Color(0xFF6C63FF), fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                    Text("Reports\nNearby", color = Color.Gray, fontSize = 10.sp)
                }
            }
        }
    }
}

@Composable
fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 2.dp)) {
        Box(modifier = Modifier.size(10.dp).background(color, RoundedCornerShape(2.dp)))
        Spacer(modifier = Modifier.width(6.dp))
        Text(label, color = Color.Gray, fontSize = 11.sp)
    }
}

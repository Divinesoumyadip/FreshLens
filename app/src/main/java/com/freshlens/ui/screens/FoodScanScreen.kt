package com.freshlens.ui.screens

import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.freshlens.ui.viewmodel.FoodScanViewModel
import com.freshlens.ui.viewmodel.ScanResult
import java.util.concurrent.Executors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodScanScreen(
    viewModel: FoodScanViewModel = hiltViewModel(),
    onNavigateToKitchenSafe: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var isScanning by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("FreshLens", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1A1A2E),
                    titleContentColor = Color.White
                ),
                actions = {
                    IconButton(onClick = { /* Navigate to history */ }) {
                        Icon(Icons.Default.History, contentDescription = "History", tint = Color.White)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0F0F1A))
                .padding(padding)
        ) {
            // Camera Preview
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(380.dp)
                    .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
            ) {
                CameraPreview(
                    modifier = Modifier.fillMaxSize(),
                    onImageCaptured = { imageProxy ->
                        viewModel.analyzeImage(imageProxy)
                    }
                )

                // Scan overlay
                ScanOverlay(isScanning = uiState.isLoading)

                // Capture button
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 24.dp)
                ) {
                    FloatingActionButton(
                        onClick = { viewModel.captureAndAnalyze() },
                        containerColor = Color(0xFF6C63FF),
                        modifier = Modifier.size(72.dp)
                    ) {
                        Icon(
                            Icons.Default.CameraAlt,
                            contentDescription = "Scan",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }

            // Results section
            AnimatedVisibility(
                visible = uiState.result != null,
                enter = slideInVertically() + fadeIn(),
                exit = slideOutVertically() + fadeOut()
            ) {
                uiState.result?.let { result ->
                    ResultCard(
                        result = result,
                        onViewKitchenSafe = { onNavigateToKitchenSafe(result.restaurantId) }
                    )
                }
            }

            // Loading state
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = Color(0xFF6C63FF))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Analyzing with 8 ML models...", color = Color.Gray, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun ResultCard(result: ScanResult, onViewKitchenSafe: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E30)),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {

            // Dish name + authenticity
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(result.dishName, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("${result.confidence}% confidence", color = Color.Gray, fontSize = 12.sp)
                }
                AuthenticityBadge(score = result.authenticityScore)
            }

            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = Color(0xFF2A2A3E))
            Spacer(modifier = Modifier.height(16.dp))

            // ML Score grid
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                ScoreChip("Fresh", result.freshnessScore, Color(0xFF4CAF50))
                ScoreChip("Portion", result.portionScore, Color(0xFF2196F3))
                ScoreChip("Value", result.valuScore, Color(0xFFFF9800))
                ScoreChip("Hygiene", result.hygieneScore, Color(0xFF9C27B0))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Nutrition breakdown
            Text("Nutrition Estimate", color = Color.White, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                NutrientBadge("Cal", "${result.calories}", Color(0xFFE91E63))
                NutrientBadge("Protein", "${result.protein}g", Color(0xFF4CAF50))
                NutrientBadge("Carbs", "${result.carbs}g", Color(0xFFFF9800))
                NutrientBadge("Fat", "${result.fat}g", Color(0xFF9C27B0))
            }

            // Allergen alerts
            if (result.allergens.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF3E1A1A)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFFF5722), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Allergens: ${result.allergens.joinToString(", ")}", color = Color(0xFFFF5722), fontSize = 13.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // KitchenSafe button
            OutlinedButton(
                onClick = onViewKitchenSafe,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF6C63FF)),
                border = BorderStroke(1.dp, Color(0xFF6C63FF))
            ) {
                Icon(Icons.Default.Shield, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("View KitchenSafe Score")
            }
        }
    }
}

@Composable
fun ScoreChip(label: String, score: Int, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(50))
                .background(color.copy(alpha = 0.15f))
                .border(2.dp, color, RoundedCornerShape(50))
        ) {
            Text("$score", color = color, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, color = Color.Gray, fontSize = 11.sp)
    }
}

@Composable
fun NutrientBadge(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = color, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        Text(label, color = Color.Gray, fontSize = 11.sp)
    }
}

@Composable
fun AuthenticityBadge(score: Int) {
    val (bgColor, label) = when {
        score >= 80 -> Pair(Color(0xFF1B5E20), "REAL")
        score >= 50 -> Pair(Color(0xFFE65100), "SUSPECT")
        else -> Pair(Color(0xFFB71C1C), "FAKE")
    }
    Surface(
        color = bgColor,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            label, color = Color.White,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            fontWeight = FontWeight.Bold, fontSize = 13.sp
        )
    }
}

@Composable
fun ScanOverlay(isScanning: Boolean) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Corner brackets
        val cornerColor = if (isScanning) Color(0xFF6C63FF) else Color.White.copy(alpha = 0.7f)
        val cornerSize = 40.dp
        val strokeWidth = 3.dp

        // Top-left
        Canvas(modifier = Modifier.align(Alignment.Center).size(200.dp)) { }

        if (isScanning) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter),
                color = Color(0xFF6C63FF),
                trackColor = Color.Transparent
            )
        }
    }
}

@Composable
fun CameraPreview(modifier: Modifier = Modifier, onImageCaptured: (ImageProxy) -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            val previewView = PreviewView(ctx)
            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }
                val imageAnalysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, preview, imageAnalysis
                    )
                } catch (e: Exception) { e.printStackTrace() }
            }, ctx.mainExecutor)
            previewView
        }
    )
}

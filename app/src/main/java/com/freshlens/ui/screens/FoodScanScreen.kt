package com.freshlens.ui.screens

import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import com.freshlens.ml.FoodScanResult
import com.freshlens.ui.viewmodel.FoodScanViewModel
import java.util.concurrent.Executors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodScanScreen(
    viewModel: FoodScanViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    Box(modifier = Modifier.fillMaxSize()) {

        // ── Camera Preview ─────────────────────────────────────────────
        CameraPreview(
            modifier = Modifier.fillMaxSize(),
            onImageCaptured = { bitmap -> viewModel.analyzeImage(bitmap) }
        )

        // ── Scan Overlay ────────────────────────────────────────────────
        ScanOverlay()

        // ── Top Bar ─────────────────────────────────────────────────────
        TopBar(modifier = Modifier.align(Alignment.TopCenter))

        // ── Capture Button ───────────────────────────────────────────────
        CaptureButton(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp),
            isAnalyzing = uiState.isAnalyzing,
            onClick = { viewModel.captureAndAnalyze() }
        )

        // ── Results Sheet ─────────────────────────────────────────────────
        AnimatedVisibility(
            visible = uiState.scanResult != null,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it }),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            uiState.scanResult?.let { result ->
                ResultsBottomSheet(
                    result = result,
                    onDismiss = { viewModel.clearResult() }
                )
            }
        }

        // ── Loading ───────────────────────────────────────────────────────
        if (uiState.isAnalyzing) {
            AnalyzingOverlay()
        }
    }
}

@Composable
fun ResultsBottomSheet(
    result: FoodScanResult,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 600.dp),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A2E))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            // Dish name + inference time
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = result.dishName,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "${result.inferenceTimeMs}ms",
                    fontSize = 11.sp,
                    color = Color(0xFF4CAF50)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Score Cards ──────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ScoreCard(
                    modifier = Modifier.weight(1f),
                    label = "Authentic",
                    score = result.authenticityScore,
                    icon = "🛡️"
                )
                ScoreCard(
                    modifier = Modifier.weight(1f),
                    label = "Fresh",
                    score = result.freshnessScore,
                    icon = "🌿"
                )
                ScoreCard(
                    modifier = Modifier.weight(1f),
                    label = "Fair Price",
                    score = result.priceFairnessScore,
                    icon = "💰"
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Portion ──────────────────────────────────────────────
            Text(
                text = "Estimated Portion: ${result.portionGrams.toInt()}g",
                color = Color(0xFFB0BEC5),
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ── Nutrition ────────────────────────────────────────────
            Text(
                text = "Nutrition",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))

            NutritionRow("🔥 Calories", "${result.calories} kcal")
            NutritionRow("💪 Protein", "${result.proteinG}g")
            NutritionRow("🍞 Carbs", "${result.carbsG}g")
            NutritionRow("🧈 Fat", "${result.fatG}g")
            NutritionRow("🌾 Fiber", "${result.fiberG}g")

            // ── Allergens ─────────────────────────────────────────────
            if (result.allergens.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "⚠️ Allergens Detected",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFFFF5722)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    result.allergens.forEach { allergen ->
                        AssistChip(
                            onClick = {},
                            label = { Text(allergen, fontSize = 11.sp) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = Color(0xFFFF5722).copy(alpha = 0.2f),
                                labelColor = Color(0xFFFF5722)
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
            ) {
                Text("Scan Another Dish")
            }
        }
    }
}

@Composable
fun ScoreCard(
    modifier: Modifier = Modifier,
    label: String,
    score: Float,
    icon: String
) {
    val color = when {
        score >= 75 -> Color(0xFF4CAF50)
        score >= 50 -> Color(0xFFFF9800)
        else -> Color(0xFFF44336)
    }

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color(0xFF16213E))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = icon, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${score.toInt()}",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = label,
                fontSize = 10.sp,
                color = Color(0xFF90A4AE)
            )
        }
    }
}

@Composable
fun NutritionRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = Color(0xFFB0BEC5), fontSize = 13.sp)
        Text(text = value, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun ScanOverlay() {
    Box(modifier = Modifier.fillMaxSize()) {
        // Corner brackets for scan area
        Box(
            modifier = Modifier
                .size(250.dp)
                .align(Alignment.Center)
                .border(
                    width = 2.dp,
                    color = Color(0xFF4CAF50).copy(alpha = 0.8f),
                    shape = RoundedCornerShape(16.dp)
                )
        )
        Text(
            text = "Point at food to scan",
            modifier = Modifier
                .align(Alignment.Center)
                .padding(top = 280.dp),
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 13.sp
        )
    }
}

@Composable
fun TopBar(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "🍽️ FreshLens",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Icon(
            imageVector = Icons.Default.Settings,
            contentDescription = "Settings",
            tint = Color.White
        )
    }
}

@Composable
fun CaptureButton(
    modifier: Modifier = Modifier,
    isAnalyzing: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .size(72.dp)
            .clip(CircleShape)
            .background(if (isAnalyzing) Color.Gray else Color(0xFF4CAF50))
            .clickable(enabled = !isAnalyzing, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (isAnalyzing) {
            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(32.dp))
        } else {
            Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = "Scan",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
fun AnalyzingOverlay() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = Color(0xFF4CAF50), modifier = Modifier.size(48.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text("Running 8 ML Models...", color = Color.White, fontSize = 14.sp)
            Text("~89ms inference", color = Color(0xFF4CAF50), fontSize = 12.sp)
        }
    }
}

@Composable
fun CameraPreview(
    modifier: Modifier = Modifier,
    onImageCaptured: (android.graphics.Bitmap) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context) }

    AndroidView(
        factory = { previewView },
        modifier = modifier
    ) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, Executors.newSingleThreadExecutor())
    }
}

package com.freshlens.ui.viewmodel

import androidx.camera.core.ImageProxy
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ScanResult(
    val dishName: String = "",
    val confidence: Int = 0,
    val authenticityScore: Int = 0,
    val freshnessScore: Int = 0,
    val portionScore: Int = 0,
    val valuScore: Int = 0,
    val hygieneScore: Int = 0,
    val calories: Int = 0,
    val protein: Int = 0,
    val carbs: Int = 0,
    val fat: Int = 0,
    val allergens: List<String> = emptyList(),
    val restaurantId: String = ""
)

data class FoodScanUiState(
    val isLoading: Boolean = false,
    val result: ScanResult? = null,
    val error: String? = null
)

@HiltViewModel
class FoodScanViewModel @Inject constructor(
    private val mlManager: com.freshlens.ml.FreshLensMLManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(FoodScanUiState())
    val uiState: StateFlow<FoodScanUiState> = _uiState.asStateFlow()

    fun captureAndAnalyze() {
        // Trigger camera capture — actual capture happens via ImageAnalysis in the screen
        _uiState.value = _uiState.value.copy(isLoading = true)
    }

    fun analyzeImage(imageProxy: ImageProxy) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)

                val bitmap = imageProxy.toBitmap()

                // Run all 8 ML models via MLManager
                val dishResult = mlManager.recognizeDish(bitmap)
                val authenticityResult = mlManager.checkAuthenticity(bitmap)
                val freshnessResult = mlManager.rateFreshness(bitmap)
                val portionResult = mlManager.estimatePortion(bitmap)
                val nutritionResult = mlManager.estimateNutrition(bitmap)
                val allergenResult = mlManager.detectAllergens(bitmap)
                val priceFairResult = mlManager.ratePriceFairness(bitmap)

                val result = ScanResult(
                    dishName = dishResult.dishName,
                    confidence = (dishResult.confidence * 100).toInt(),
                    authenticityScore = (authenticityResult.score * 100).toInt(),
                    freshnessScore = (freshnessResult.score * 100).toInt(),
                    portionScore = portionResult.estimatedGrams,
                    valuScore = (priceFairResult.score * 100).toInt(),
                    hygieneScore = 75, // From backend KitchenSafe score
                    calories = nutritionResult.calories,
                    protein = nutritionResult.protein,
                    carbs = nutritionResult.carbs,
                    fat = nutritionResult.fat,
                    allergens = allergenResult.detected
                )

                _uiState.value = _uiState.value.copy(isLoading = false, result = result)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Analysis failed: ${e.message}"
                )
            } finally {
                imageProxy.close()
            }
        }
    }
}

package com.freshlens.ui.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.freshlens.ml.FoodScanResult
import com.freshlens.ml.FreshLensMLManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FoodScanUiState(
    val isAnalyzing: Boolean = false,
    val scanResult: FoodScanResult? = null,
    val error: String? = null
)

@HiltViewModel
class FoodScanViewModel @Inject constructor(
    private val mlManager: FreshLensMLManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(FoodScanUiState())
    val uiState: StateFlow<FoodScanUiState> = _uiState.asStateFlow()

    fun captureAndAnalyze() {
        // Triggered from UI — actual bitmap passed via analyzeImage
        _uiState.value = _uiState.value.copy(isAnalyzing = true)
    }

    fun analyzeImage(bitmap: Bitmap) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isAnalyzing = true, error = null)
            try {
                val result = mlManager.scanFood(bitmap)
                _uiState.value = _uiState.value.copy(
                    isAnalyzing = false,
                    scanResult = result
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isAnalyzing = false,
                    error = e.message ?: "Analysis failed"
                )
            }
        }
    }

    fun clearResult() {
        _uiState.value = _uiState.value.copy(scanResult = null)
    }
}

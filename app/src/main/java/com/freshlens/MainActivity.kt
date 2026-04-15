package com.freshlens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.freshlens.ui.screens.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FreshLensApp()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FreshLensApp() {
    val navController = rememberNavController()
    var selectedTab by remember { mutableStateOf(0) }

    val tabs = listOf(
        Triple("Scan", Icons.Default.CameraAlt, "scan"),
        Triple("HeatMap", Icons.Default.Map, "heatmap"),
        Triple("Reviews", Icons.Default.Shield, "reviews"),
    )

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = Color(0xFF1A1A2E)) {
                tabs.forEachIndexed { index, (label, icon, route) ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = {
                            selectedTab = index
                            navController.navigate(route) {
                                popUpTo("scan") { inclusive = false }
                                launchSingleTop = true
                            }
                        },
                        icon = { Icon(icon, contentDescription = label) },
                        label = { Text(label, fontWeight = FontWeight.Medium) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF6C63FF),
                            selectedTextColor = Color(0xFF6C63FF),
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray,
                            indicatorColor = Color(0xFF2A2A4E)
                        )
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = "scan",
            modifier = Modifier.padding(padding)
        ) {
            composable("scan") {
                FoodScanScreen(
                    onNavigateToKitchenSafe = { restaurantId ->
                        navController.navigate("kitchen/$restaurantId")
                    }
                )
            }
            composable("heatmap") { HeatMapScreen(onBack = { navController.popBackStack() }) }
            composable("reviews") { ReviewGuardScreen(onBack = { navController.popBackStack() }) }
            composable("kitchen/{restaurantId}") { backStackEntry ->
                KitchenSafeScreen(
                    restaurantId = backStackEntry.arguments?.getString("restaurantId") ?: "",
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
